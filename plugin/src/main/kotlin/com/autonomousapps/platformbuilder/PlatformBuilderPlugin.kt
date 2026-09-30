/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.autonomousapps.platformbuilder

import com.autonomousapps.platformbuilder.PlatformBuilderPlugin.ComponentAndVariant.Kind
import com.autonomousapps.platformbuilder.internal.utils.attributes.AarJarCompatibilityRule
import com.autonomousapps.platformbuilder.internal.utils.attributes.AndroidJavaCompatibilityRule
import com.autonomousapps.platformbuilder.internal.utils.attributes.JvmPluginServices
import com.autonomousapps.platformbuilder.internal.utils.attributes.isJavaPlatform
import com.autonomousapps.platformbuilder.internal.utils.configurations.ConfigurationServices
import com.autonomousapps.platformbuilder.internal.utils.dependencies.newProjectDependency
import com.autonomousapps.platformbuilder.internal.utils.provenance.buildReason
import com.google.common.graph.ElementOrder
import com.google.common.graph.Graph
import com.google.common.graph.GraphBuilder
import com.google.common.graph.ImmutableGraph
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.dsl.DependencyConstraintFactory
import org.gradle.api.artifacts.dsl.DependencyFactory
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.artifacts.result.ResolvedVariantResult
import org.gradle.api.artifacts.result.UnresolvedDependencyResult
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.plugins.JavaPlatformExtension
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import java.util.Collections
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.collections.ArrayDeque

/**
 * A plugin that builds a Java platform based on a set of user-declared root dependencies.
 *
 * The resulting platform contains entries for all root dependencies and their transitive dependencies. Conflict
 * resolution occurs when building the platform, so any version conflicts encountered will be resolved, with the results
 * included in the platform.
 *
 * *build.gradle.kts*
 * ```
 * plugins {
 *   id("com.autonomousapps.platform-builder")
 * }
 * ```
 *
 * @see <a href="https://github.com/gradle/gradle/pull/38879/changes#diff-e6c25176a72258f0ddf524b667ff6e5ef807bbbfef115dd469baab7fc3435fac>Create JavaPlatformBuilderPlugin</a>
 */
@Suppress("UnstableApiUsage")
public abstract class PlatformBuilderPlugin @Inject constructor(
  private val dependencyConstraintFactory: DependencyConstraintFactory,
  private val dependencyFactory: DependencyFactory,
  private val dependencyHandler: DependencyHandler,
) : Plugin<Project> {
  override fun apply(target: Project): Unit = target.run {
    pluginManager.apply("java-platform")

    // nb: difference from Gradle PR (it doesn't have an extension)
    PlatformBuilderExtension.create(this)

    val configurationServices = ConfigurationServices()
    val jvmPluginServices = JvmPluginServices(objects)

    // nb: difference from Gradle PR (it doesn't automatically set allowDependencies())
    extensions.configure(JavaPlatformExtension::class.java) {
      it.allowDependencies()
    }

    val platformApi = configurations.dependencyScope(PLATFORM_API) { c ->
      c.description = "The declared dependencies to resolve the platform API graph from."
    }
    val apiClasspath = configurations.resolvable("platformApiClasspath") { c ->
      c.description = "The classpath that resolves the Java variant of the API graph for the platform."
      configurationServices.extendsFrom(c, platformApi)
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      jvmPluginServices.configureAsCompileClasspath(c)
    }
    // nb: difference from Gradle PR (no support for Android variants)
    val androidApiClasspath = configurations.resolvable("platformAndroidApiClasspath") { c ->
      c.description = "The classpath that resolves the Android variant of the API graph for the platform."
      configurationServices.extendsFrom(c, platformApi)
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      jvmPluginServices.configureAsAndroidCompileClasspath(c)
    }

    val platformRuntime = configurations.dependencyScope(PLATFORM_RUNTIME) { c ->
      c.description = "The additional declared dependencies to resolve the platform runtime graph from."
    }
    val runtimeClasspath = configurations.resolvable("platformRuntimeClasspath") { c ->
      c.description = "The classpath that resolves the Java variant of the runtime graph for the platform."
      configurationServices.extendsFrom(c, platformApi, platformRuntime)
      c.shouldResolveConsistentlyWith(apiClasspath.get())
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      jvmPluginServices.configureAsRuntimeClasspath(c)
    }
    // nb: difference from Gradle PR (no support for Android variants)
    val androidRuntimeClasspath = configurations.resolvable("platformAndroidRuntimeClasspath") { c ->
      c.description = "The classpath that resolves the Android variant of the runtime graph for the platform."
      configurationServices.extendsFrom(c, platformApi, platformRuntime)
      c.shouldResolveConsistentlyWith(androidApiClasspath.get())
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      jvmPluginServices.configureAsAndroidRuntimeClasspath(c)
    }

    val javaApiDependenciesResult = getDependencies(apiClasspath)
    val androidApiDependenciesResult = getDependencies(androidApiClasspath)
    val javaApiConstraints = javaApiDependenciesResult.map(GetDependenciesResult::constraints)
    val javaApiDependencies = javaApiDependenciesResult.map(GetDependenciesResult::dependencies)
    val androidApiConstraints = androidApiDependenciesResult.map(GetDependenciesResult::constraints)
    val androidApiDependencies = androidApiDependenciesResult.map(GetDependenciesResult::dependencies)

    // Resolve the platform graphs and add them as dependency constraints to the platform variants.
    configurations.named(JavaPlatformPlugin.API_CONFIGURATION_NAME).configure { c ->
      c.dependencyConstraints.addAllLater(javaApiConstraints.map { constraints -> constraints.map { it.dependencyConstraint } })
      // nb: difference from Gradle PR (it has no support for direct platform dependencies)
      c.dependencies.addAllLater(javaApiDependencies.map { dependencies -> dependencies.map { it.dependency } })

      // nb: difference from Gradle PR (it has no support for Android library dependencies)
      c.dependencyConstraints.addAllLater(androidApiConstraints.map { it.map { it.dependencyConstraint } })
      c.dependencies.addAllLater(androidApiDependencies.map { it.map { it.dependency } })
    }

    // TODO: the runtime graph requires special consideration re the `because` (provenance) string.
    configurations.named(JavaPlatformPlugin.RUNTIME_CONFIGURATION_NAME).configure { c ->
      val javaDependenciesResult = getDependencies(runtimeClasspath)
      val javaConstraints = javaDependenciesResult.map(GetDependenciesResult::constraints)
      val javaDependencies = javaDependenciesResult.map(GetDependenciesResult::dependencies)

      val androidDependenciesResult = getDependencies(androidRuntimeClasspath)
      val androidConstraints = androidDependenciesResult.map(GetDependenciesResult::constraints)
      val androidDependencies = androidDependenciesResult.map(GetDependenciesResult::dependencies)

      // TODO: simplify
      // TODO: does usage of `zip` break this? Probably. Maybe a map/flatMap would be better
      val jConstraints = javaConstraints.flatMap { runtime ->
        javaApiConstraints.map { api ->
          runtime.map { r ->
            val reason = api.find { it.dependencyConstraint == r.dependencyConstraint }?.reason
            r.dependencyConstraint.apply { reason?.let { because(it) } }
          }
        }
      }
//      val jConstraints = javaConstraints.zip(javaApiConstraints) { runtime, api ->
//        runtime.map { r ->
//          val reason = api.find { it.dependencyConstraint == r.dependencyConstraint }?.reason
//          r.dependencyConstraint.apply { reason?.let { because(it) } }
//        }
//      }
      val jDependencies = javaDependencies.flatMap { runtime ->
        javaApiDependencies.map { api ->
          runtime.map { r ->
            val reason = api.find { it.dependency == r.dependency }?.reason
            r.dependency.apply { reason?.let { because(it) } }
          }
        }
      }
//      val jDependencies = javaDependencies.zip(javaApiDependencies) { runtime, api ->
//        runtime.map { r ->
//          val reason = api.find { it.dependency == r.dependency }?.reason
//          r.dependency.apply { reason?.let { because(it) } }
//        }
//      }
      val aConstraints = androidConstraints.flatMap { runtime ->
        androidApiConstraints.map { api ->
          runtime.map { r ->
            val reason = api.find { it.dependencyConstraint == r.dependencyConstraint }?.reason
            r.dependencyConstraint.apply { reason?.let { because(it) } }
          }
        }
      }
//      val aConstraints = androidConstraints.zip(androidApiConstraints) { runtime, api ->
//        runtime.map { r ->
//          val reason = api.find { it.dependencyConstraint == r.dependencyConstraint }?.reason
//          r.dependencyConstraint.apply { reason?.let { because(it) } }
//        }
//      }
      val aDependencies = androidDependencies.flatMap { runtime ->
        androidApiDependencies.map { api ->
          runtime.map { r ->
            val reason = api.find { it.dependency == r.dependency }?.reason
            r.dependency.apply { reason?.let { because(it) } }
          }
        }
      }
//      val aDependencies = androidDependencies.zip(androidApiDependencies) { runtime, api ->
//        runtime.map { r ->
//          val reason = api.find { it.dependency == r.dependency }?.reason
//          r.dependency.apply { reason?.let { because(it) } }
//        }
//      }

      c.dependencyConstraints.addAllLater(jConstraints)
//      c.dependencyConstraints.addAllLater(javaConstraints.map { constraints -> constraints.map { it.dependencyConstraint } })
      // nb: difference from Gradle PR (it has no support for direct platform dependencies)
      c.dependencies.addAllLater(jDependencies)
//      c.dependencies.addAllLater(javaDependencies.map { dependencies -> dependencies.map { it.dependency } })

      // nb: difference from Gradle PR (it has no support for Android library dependencies)
      c.dependencyConstraints.addAllLater(aConstraints)
//      c.dependencyConstraints.addAllLater(androidConstraints.map { constraints -> constraints.map { it.dependencyConstraint } })
      c.dependencies.addAllLater(aDependencies)
//      c.dependencies.addAllLater(androidDependencies.map { dependencies -> dependencies.map { it.dependency } })
    }

    // nb: difference from Gradle PR (it has no support for Android library dependencies or Kotlin platform type)
    dependencyHandler.run {
      attributesSchema.run {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE).run {
          compatibilityRules.add(AarJarCompatibilityRule::class.java)
        }
        // This one never seems to trigger, but also it feels right to define it.
        attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE).run {
          compatibilityRules.add(AndroidJavaCompatibilityRule::class.java)
        }

        // Requires org.jetbrains.kotlin:kotlin-gradle-plugin-api on the classpath
        if (isKgpAvailable()) {
          KotlinPlatformType.setupAttributesMatchingStrategy(this)
        }
      }
    }
  }

  // nb: difference from Gradle PR (it has no support for Kotlin platform type)
  private fun isKgpAvailable(): Boolean {
    return try {
      Class.forName("org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType", false, javaClass.classLoader)
      true
    } catch (_: ClassNotFoundException) {
      false
    }
  }

  /**
   * Given a configuration, for each component in its resolved graph, return a dependency constraint for that component.
   *
   * nb: difference from Gradle PR (it has no support for direct platform dependencies).
   */
  private fun Project.getDependencies(
    graphConfiguration: NamedDomainObjectProvider<ResolvableConfiguration>,
  ): Provider<GetDependenciesResult> {
    return graphConfiguration
      .flatMap { c ->
        c.incoming.resolutionResult.rootComponent
          .zip(c.incoming.resolutionResult.rootVariant) { resolvedComponentResult, resolvedVariantResult ->
            ComponentAndVariant(resolvedComponentResult, resolvedVariantResult, Kind.REGULAR)
          }
      }
      .map { root ->
        val result = getComponentIds(root)
        val rootId = root.component.id
        val provenance = result.provenance

        val constraints = result.regularComponents.stream()
          .filter(excludeGuava)
          .map { componentId ->
            val reason = buildReason(rootId, componentId, provenance)

            when (componentId) {
              is ModuleComponentIdentifier -> {
                val constraint =
                  dependencyConstraintFactory.create("${componentId.group}:${componentId.module}:${componentId.version}")
                    // nb: difference from Gradle PR (it has no support for tracking provenance)
                    .apply { because(reason) }

                ReasonedDependencyConstraint(constraint, reason)
              }

              is ProjectComponentIdentifier -> {
                val constraint = dependencyConstraintFactory.create(newProjectDependency(componentId.projectPath))
                  // nb: difference from Gradle PR (it has no support for tracking provenance)
                  .apply { because(reason) }

                ReasonedDependencyConstraint(constraint, reason)
              }

              else -> {
                throw GradleException("Unsupported component type '${componentId.javaClass.name}': ${componentId.displayName}")
              }
            }
          }
          .collect(Collectors.toList())

        val dependencies = result.platformComponents.stream()
          .filter(excludeGuava)
          .map { componentId ->
            val reason = buildReason(rootId, componentId, provenance)

            when (componentId) {
              is ModuleComponentIdentifier -> {
                root.variant.owner
                root.component

                val dependency = dependencyHandler
                  .platform(dependencyFactory.create("${componentId.group}:${componentId.module}:${componentId.version}"))
                  // nb: difference from Gradle PR (it has no support for tracking provenance)
                  .apply { because(reason) }

                ReasonedDependency(dependency, reason)

              }

              is ProjectComponentIdentifier -> {
                val dependency = dependencyHandler.platform(newProjectDependency(componentId.projectPath))
                  // nb: difference from Gradle PR (it has no support for tracking provenance)
                  .apply { because(reason) }

                ReasonedDependency(dependency, reason)
              }

              else -> {
                throw GradleException("Unsupported component type '${componentId.javaClass.name}': ${componentId.displayName}")
              }
            }
          }
          .collect(Collectors.toList())

        GetDependenciesResult(
          constraints = constraints,
          dependencies = dependencies,
        )
      }
  }

  /**
   * Exclude `com.google.guava:guava` and `com.google.guava:listenablefuture` from the platform. These are problematic
   * dependencies.
   *
   * nb: difference from Gradle PR (it does not and would not do this).
   *
   * TODO(tsr): this could be configurable
   */
  private val excludeGuava: (ComponentIdentifier) -> Boolean = { componentId ->
    if (componentId is ModuleComponentIdentifier) {
      val identifier = "${componentId.group}:${componentId.module}"
      identifier !in GUAVA
    } else {
      true
    }
  }

  /** A variant, the component it belongs to, and its [kind][Kind] (regular or platform). */
  private data class ComponentAndVariant(
    val component: ResolvedComponentResult,
    val variant: ResolvedVariantResult,
    val kind: Kind,
  ) {
    enum class Kind {
      REGULAR, PLATFORM
    }
  }

  // nb: difference from Gradle PR (it has no support for direct platform dependencies)
  // TODO: consider returning a stronger type than Collection. It could simplify the mapping above.
  private class GetDependenciesResult(
    val constraints: Collection<ReasonedDependencyConstraint>,
    val dependencies: Collection<ReasonedDependency>,
  )

  private class ReasonedDependencyConstraint(
    val dependencyConstraint: DependencyConstraint,
    val reason: String,
  )

  private class ReasonedDependency(
    val dependency: Dependency,
    val reason: String,
  )

  // nb: difference from Gradle PR (it has no support for direct platform dependencies, nor provenance)
  private class GetComponentIdsResult(
    val regularComponents: Set<ComponentIdentifier>,
    val platformComponents: Set<ComponentIdentifier>,
    val provenance: Graph<ComponentIdentifier>,
  )

  public companion object {

    public const val PLATFORM_API: String = "platformApi"
    public const val PLATFORM_RUNTIME: String = "platformRuntime"

    private val GUAVA = listOf("com.google.guava:listenablefuture", "com.google.guava:guava")

    /**
     * Walks a dependency graph BFS from the root, returning the IDs of all components present, in the order they were
     * encountered.
     *
     * nb: difference from Gradle PR (it has no support for direct platform dependencies).
     */
    private fun getComponentIds(root: ComponentAndVariant): GetComponentIdsResult {
      // These are the things that get returned
      val seenComponents = linkedSetOf<ComponentIdentifier>()
      val seenPlatformComponents = linkedSetOf<ComponentIdentifier>()
      // nb: difference from Gradle PR (it has no support for tracking provenance)
      val provenance: ImmutableGraph.Builder<ComponentIdentifier> = GraphBuilder.directed()
        .allowsSelfLoops(false)
        .incidentEdgeOrder(ElementOrder.stable<ComponentIdentifier>())
        .immutable()

      val seenVariants = mutableSetOf<ResolvedVariantResult>()
      val queue = ArrayDeque<ComponentAndVariant>()

      seenVariants.add(root.variant)
      queue.add(root)

      while (queue.isNotEmpty()) {
        val next = queue.removeFirst()

        // These are the things that get returned
        // Treat normal and platform dependencies differently
        if (next.kind == Kind.REGULAR) {
          seenComponents.add(next.component.id)
        } else {
          seenPlatformComponents.add(next.component.id)
        }

        // nb: difference from Gradle PR (it has no support for direct platform dependencies)
        if (next.kind == Kind.PLATFORM) {
          // Don't add platforms' dependencies. They provide those themselves.
          continue
        }

        for (dependency in next.component.getDependenciesForVariant(next.variant)) {
          if (dependency is ResolvedDependencyResult) {
            val selectedComponent = dependency.selected
            val resolvedVariant = dependency.resolvedVariant

            if (seenVariants.add(resolvedVariant)) {
              // nb: difference from Gradle PR (it has no support for direct platform dependencies)
              val kind = if (dependency.isJavaPlatform()) Kind.PLATFORM else Kind.REGULAR
              queue.add(ComponentAndVariant(selectedComponent, resolvedVariant, kind))
            }

            // If the selected component is the requested component, then we can say that the requested component's
            // incoming edge is the "reason" for it—this helps us track provenance.
            // nb: difference from Gradle PR (it has no support for tracking provenance)
            // nb: strict matching is imperfect (doesn't handle dynamic versions)
            // https://github.com/gradle/gradle/blob/v9.8.0/platforms/software/dependency-management/src/main/java/org/gradle/internal/component/external/model/DefaultModuleComponentSelector.java#L144
            val isRequested = dependency.requested.matchesStrictly(selectedComponent.id)
            if (isRequested) {
              val incomingEdge = dependency.from.id
              provenance.putEdge(incomingEdge, selectedComponent.id)
            }
          } else if (dependency is UnresolvedDependencyResult) {
            throw GradleException("Failed to build platform.", dependency.failure)
          }
        }
      }

      // The platform should not constrain itself.
      seenComponents.remove(root.component.id)

      return GetComponentIdsResult(
        regularComponents = Collections.unmodifiableSet(seenComponents),
        platformComponents = Collections.unmodifiableSet(seenPlatformComponents),
        provenance = provenance.build(),
      )
    }
  }
}
