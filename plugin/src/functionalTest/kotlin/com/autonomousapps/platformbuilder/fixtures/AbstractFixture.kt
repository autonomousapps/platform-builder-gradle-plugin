// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.AbstractGradleProject
import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Dependency
import com.autonomousapps.kit.gradle.GradleProperties
import com.autonomousapps.kit.gradle.Plugin
import org.gradle.util.GradleVersion

internal abstract class AbstractFixture(
  protected val gradleVersion: GradleVersion,
) : AbstractGradleProject() {

  companion object {
    const val AGP_VERSION = "9.0.1"
    const val KGP_VERSION = "2.4.10"

    private const val PLATFORM_BUILDER_ID = "com.autonomousapps.platform-builder"

    /*
     * These value versions and `apply = false` because they're applied to the settings script, simply to add them to
     * the build classpath (not yet apply them).
     */
    private val AGP = Plugin("com.android.application", AGP_VERSION, apply = false)
    private val KGP = Plugin("org.jetbrains.kotlin.jvm", KGP_VERSION, apply = false)
    internal val PLATFORM_BUILDER = Plugin(PLATFORM_BUILDER_ID, PLUGIN_UNDER_TEST_VERSION, apply = false)


    val ANDROID_APP = Plugin("com.android.application")
    val ANDROID_LIB = Plugin("com.android.library")
    val ANDROID_TEST = Plugin("com.android.test")
    val JAVA_LIB = Plugin("java-library")
    val JAVA_PLATFORM = Plugin("java-platform")
    val KOTLIN_JVM = Plugin("org.jetbrains.kotlin.jvm")

    /** Apply to project build scripts. */
    val PLATFORM_BUILDER_PLUGIN = Plugin(PLATFORM_BUILDER_ID)

    const val COMPILE_SDK = 34

    val JUNIT_PLATFORM = Dependency.testImplementation("org.junit:junit-bom:5.14.4").onPlatform()
    val JUNIT_API = Dependency.testImplementation("org.junit.jupiter:junit-jupiter-api")
    val JUNIT_ENGINE = Dependency.testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    val JUNIT_LAUNCHER = Dependency.testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    val JUNIT4 = Dependency.testImplementation("junit:junit:4.13.2")

    @JvmStatic
    protected fun platformApi(dependencyNotation: String): Dependency = Dependency("platformApi", dependencyNotation)
  }

  override fun newGradleProjectBuilder(): GradleProject.Builder {
    val properties = listOf(
      GradleProperties.BUILD_CACHE,
      GradleProperties.CONFIGURATION_CACHE_STABLE,
//      GradleProperties.ISOLATED_PROJECTS,
      GradleProperties.PARALLEL,
    )

    return super.newGradleProjectBuilder()
      .withRootProject {
        gradleProperties += properties
        withSettingsScript {
          plugins(PLATFORM_BUILDER, AGP, KGP)
        }
      }
  }
}
