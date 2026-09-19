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
import com.autonomousapps.platformbuilder.internal.utils.attributes.isJavaPlatform
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
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.HasConfigurableAttributes
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.plugins.JavaPlatformExtension
import org.gradle.api.plugins.JavaPlatformPlugin
import org.gradle.api.provider.Provider
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

    // nb: difference from Gradle PR (it doesn't automatically set allowDependencies())
    extensions.configure(JavaPlatformExtension::class.java) {
      it.allowDependencies()
    }

    val platformApi = configurations.dependencyScope("platformApi") { c ->
      c.description = "The declared dependencies to resolve the platform API graph from."
    }
    val apiClasspath = configurations.resolvable("platformApiClasspath") { c ->
      c.description = "The classpath that resolves the Java variant of the API graph for the platform."
      c.extendsFrom(platformApi)
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      configureAsCompileClasspath(c)
    }
    // nb: difference from Gradle PR (no support for Android variants)
    val androidApiClasspath = configurations.resolvable("platformAndroidApiClasspath") { c ->
      c.description = "The classpath that resolves the Android variant of the API graph for the platform."
      c.extendsFrom(platformApi)
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      configureAsAndroidCompileClasspath(c)
    }

    val platformRuntime = configurations.dependencyScope("platformRuntime") { c ->
      c.description = "The additional declared dependencies to resolve the platform runtime graph from."
    }
    val runtimeClasspath = configurations.resolvable("platformRuntimeClasspath") { c ->
      c.description = "The classpath that resolves the Java variant of the runtime graph for the platform."
      c.extendsFrom(platformApi, platformRuntime)
      c.shouldResolveConsistentlyWith(apiClasspath.get())
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      configureAsRuntimeClasspath(c)
    }
    // nb: difference from Gradle PR (no support for Android variants)
    val androidRuntimeClasspath = configurations.resolvable("platformAndroidRuntimeClasspath") { c ->
      c.description = "The classpath that resolves the Android variant of the runtime graph for the platform."
      c.extendsFrom(platformApi, platformRuntime)
      c.shouldResolveConsistentlyWith(androidApiClasspath.get())
      // nb: difference from Gradle PR (it uses JvmPluginServices)
      configureAsAndroidRuntimeClasspath(c)
    }

    // Resolve the platform graphs and add them as dependency constraints to the platform variants.
    configurations.named(JavaPlatformPlugin.API_CONFIGURATION_NAME).configure { c ->
      val javaDependenciesResult = getDependencies(apiClasspath)
      val javaConstraints = javaDependenciesResult.map(GetDependenciesResult::constraints)
      val javaDependencies = javaDependenciesResult.map(GetDependenciesResult::dependencies)

      val androidDependenciesResult = getDependencies(androidApiClasspath)
      val androidConstraints = androidDependenciesResult.map(GetDependenciesResult::constraints)
      val androidDependencies = androidDependenciesResult.map(GetDependenciesResult::dependencies)

      c.dependencyConstraints.addAllLater(javaConstraints)
      // nb: difference from Gradle PR (it has no support for direct platform dependencies)
      c.dependencies.addAllLater(javaDependencies)

      // nb: difference from Gradle PR (it has no support for Android library dependencies)
      c.dependencyConstraints.addAllLater(androidConstraints)
      c.dependencies.addAllLater(androidDependencies)
    }
    configurations.named(JavaPlatformPlugin.RUNTIME_CONFIGURATION_NAME).configure { c ->
      val javaDependenciesResult = getDependencies(runtimeClasspath)
      val javaConstraints = javaDependenciesResult.map(GetDependenciesResult::constraints)
      val javaDependencies = javaDependenciesResult.map(GetDependenciesResult::dependencies)

      val androidDependenciesResult = getDependencies(androidRuntimeClasspath)
      val androidConstraints = androidDependenciesResult.map(GetDependenciesResult::constraints)
      val androidDependencies = androidDependenciesResult.map(GetDependenciesResult::dependencies)

      c.dependencyConstraints.addAllLater(javaConstraints)
      // nb: difference from Gradle PR (it has no support for direct platform dependencies)
      c.dependencies.addAllLater(javaDependencies)

      // nb: difference from Gradle PR (it has no support for Android library dependencies)
      c.dependencyConstraints.addAllLater(androidConstraints)
      c.dependencies.addAllLater(androidDependencies)
    }

    // nb: difference from Gradle PR (it has no support for Android library dependencies)
    dependencyHandler.run {
      attributesSchema.run {
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE).run {
          compatibilityRules.add(AarJarCompatibilityRule::class.java)
        }
        // This one never seems to trigger, but also it feels right to define it.
        attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE).run {
          compatibilityRules.add(AndroidJavaCompatibilityRule::class.java)
        }
      }
    }
  }

  private fun configureAsAndroidCompileClasspath(configuration: HasConfigurableAttributes<*>) {
    with(configuration.attributes) {
      attributes.attribute(Category.CATEGORY_ATTRIBUTE, attributes.named(Category::class.java, Category.LIBRARY))
      attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_API))
      attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, attributes.named(Bundling::class.java, Bundling.EXTERNAL))
      attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.ANDROID))
    }
  }

  private fun configureAsAndroidRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
    with(configuration.attributes) {
      attributes.attribute(Category.CATEGORY_ATTRIBUTE, attributes.named(Category::class.java, Category.LIBRARY))
      attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_RUNTIME))
      attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attributes.named(LibraryElements::class.java, "aar"))
      attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, attributes.named(Bundling::class.java, Bundling.EXTERNAL))
      attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.ANDROID))
    }
  }

  /** Public API version of `jvmPluginServices.configureAsCompileClasspath(conf)`. */
  private fun configureAsCompileClasspath(configuration: HasConfigurableAttributes<*>) {
    //this.configureAttributes(configuration, (details) -> details.library().apiUsage().withExternalDependencies().preferStandardJVM());
    with(configuration.attributes) {
      attributes.attribute(Category.CATEGORY_ATTRIBUTE, attributes.named(Category::class.java, Category.LIBRARY))
      attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_API))
      attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, attributes.named(Bundling::class.java, Bundling.EXTERNAL))
      attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM))
    }
  }

  /** Public API version of `jvmPluginServices.configureAsRuntimeClasspath(conf)`. */
  private fun configureAsRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
    //this.configureAttributes(configuration, (details) -> details.library().runtimeUsage().asJar().withExternalDependencies().preferStandardJVM());
    with(configuration.attributes) {
      attributes.attribute(Category.CATEGORY_ATTRIBUTE, attributes.named(Category::class.java, Category.LIBRARY))
      attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_RUNTIME))
      attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, attributes.named(LibraryElements::class.java, LibraryElements.JAR))
      attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, attributes.named(Bundling::class.java, Bundling.EXTERNAL))
      attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM))
    }
  }

  /**
   * Given a configuration, for each component in its resolved graph, return a dependency constraint for that component.
   *
   * nb: difference from Gradle PR (it has no support for direct platform dependencies).
   */
  private fun Project.getDependencies(graphConfiguration: NamedDomainObjectProvider<ResolvableConfiguration>): Provider<GetDependenciesResult> {
    return graphConfiguration
      .flatMap { c ->
        c.incoming.resolutionResult.rootComponent
          .zip(c.incoming.resolutionResult.rootVariant) { resolvedComponentResult, resolvedVariantResult ->
            ComponentAndVariant(resolvedComponentResult, resolvedVariantResult, Kind.REGULAR)
          }
      }
      .map { root ->
        // TODO(from Justin's PR) We should have an extension in this plugin that lets you optionally make these all
        //  strict versions. Or we should add a strictPlatform wrapper, similar to enforcedPlatform, that lets you
        //  interpret a platform as all strict versions.
        val result = getComponentIds(root)

        val constraints = result.regularComponents.stream().map { componentId ->
          when (componentId) {
            is ModuleComponentIdentifier -> {
              dependencyConstraintFactory.create("${componentId.group}:${componentId.module}:${componentId.version}")
            }

            is ProjectComponentIdentifier -> {
              dependencyConstraintFactory.create(dependencyFactory.createProjectDependency(componentId.projectPath))
            }

            else -> {
              throw GradleException("Unsupported component type '${componentId.javaClass.name}': ${componentId.displayName}")
            }
          }
        }.collect(Collectors.toList())

        val dependencies = result.platformComponents.stream().map { componentId ->
          when (componentId) {
            is ModuleComponentIdentifier -> {
              dependencyHandler.platform(dependencyFactory.create("${componentId.group}:${componentId.module}:${componentId.version}"))
            }

            is ProjectComponentIdentifier -> {
              dependencyHandler.platform(dependencyFactory.createProjectDependency(componentId.projectPath))
            }

            else -> {
              throw GradleException("Unsupported component type '${componentId.javaClass.name}': ${componentId.displayName}")
            }
          }
        }.collect(Collectors.toList())

        GetDependenciesResult(
          constraints = constraints,
          dependencies = dependencies,
        )
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
  private class GetDependenciesResult(
    val constraints: Collection<DependencyConstraint>,
    val dependencies: Collection<Dependency>,
  )

  // nb: difference from Gradle PR (it has no support for direct platform dependencies)
  private class GetComponentIdsResult(
    val regularComponents: Set<ComponentIdentifier>,
    val platformComponents: Set<ComponentIdentifier>,
  )

  private companion object {
    /**
     * Walks a dependency graph BFS from the root, returning the IDs of all components present, in the order they were
     * encountered.
     *
     * nb: difference from Gradle PR (it has no support for direct platform dependencies).
     */
    fun getComponentIds(root: ComponentAndVariant): GetComponentIdsResult {
      val seenComponents = linkedSetOf<ComponentIdentifier>()
      val seenPlatformComponents = linkedSetOf<ComponentIdentifier>()

      val seenVariants = mutableSetOf<ResolvedVariantResult>()
      val queue = ArrayDeque<ComponentAndVariant>()

      seenVariants.add(root.variant)
      queue.add(root)

      while (queue.isNotEmpty()) {
        val next = queue.removeFirst()

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
            val component = dependency.selected
            val variant = dependency.resolvedVariant

            if (seenVariants.add(variant)) {
              // nb: difference from Gradle PR (it has no support for direct platform dependencies)
              val kind = if (dependency.isJavaPlatform()) Kind.PLATFORM else Kind.REGULAR
              queue.add(ComponentAndVariant(component, variant, kind))
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
      )
    }
  }
}
