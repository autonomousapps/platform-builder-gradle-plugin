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
@file:Suppress("UnstableApiUsage")

package com.autonomousapps.platformbuilder.internal.utils.attributes

import com.autonomousapps.platformbuilder.internal.utils.versions.GradleVersions
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.HasConfigurableAttributes
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.java.TargetJvmEnvironment
import org.gradle.api.model.ObjectFactory

/*
 * These functions were borrowed and modified (to support Android and Gradle < 9.3.0) from
 * `org.gradle.api.plugins.jvm.internal.JvmPluginServices`.
 */

internal class JvmPluginServices(
  private val objects: ObjectFactory,
) {

  private val isAtLeastGradle930 = GradleVersions.isAtLeast930

  fun configureAsAndroidCompileClasspath(configuration: HasConfigurableAttributes<*>) {
    configuration.library().apiUsage().asAar().withExternalDependencies().preferAndroid()
  }

  fun configureAsAndroidRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
    configuration.library().runtimeUsage().asAar().withExternalDependencies().preferAndroid()
  }

  fun configureAsCompileClasspath(configuration: HasConfigurableAttributes<*>) {
    configuration.library().apiUsage().asJar().withExternalDependencies().preferStandardJVM()
  }

  fun configureAsRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
    configuration.library().runtimeUsage().asJar().withExternalDependencies().preferStandardJVM()
  }

  private fun HasConfigurableAttributes<*>.library(): HasConfigurableAttributes<*> {
    val category = if (isAtLeastGradle930) {
      attributes.named(Category::class.java, Category.LIBRARY)
    } else {
      objects.named(Category::class.java, Category.LIBRARY)
    }
    attributes.attribute(Category.CATEGORY_ATTRIBUTE, category)
    return this
  }

  private fun HasConfigurableAttributes<*>.apiUsage(): HasConfigurableAttributes<*> {
    val usage = if (isAtLeastGradle930) {
      attributes.named(Usage::class.java, Usage.JAVA_API)
    } else {
      objects.named(Usage::class.java, Usage.JAVA_API)
    }
    attributes.attribute(Usage.USAGE_ATTRIBUTE, usage)
    return this
  }

  private fun HasConfigurableAttributes<*>.runtimeUsage(): HasConfigurableAttributes<*> {
    val usage = if (isAtLeastGradle930) {
      attributes.named(Usage::class.java, Usage.JAVA_RUNTIME)
    } else {
      objects.named(Usage::class.java, Usage.JAVA_RUNTIME)
    }
    attributes.attribute(Usage.USAGE_ATTRIBUTE, usage)
    return this
  }

  private fun HasConfigurableAttributes<*>.withExternalDependencies(): HasConfigurableAttributes<*> {
    val bundling = if (isAtLeastGradle930) {
      attributes.named(Bundling::class.java, Bundling.EXTERNAL)
    } else {
      objects.named(Bundling::class.java, Bundling.EXTERNAL)
    }
    attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, bundling)
    return this
  }

  private fun HasConfigurableAttributes<*>.asAar(): HasConfigurableAttributes<*> {
    val libraryElements = if (isAtLeastGradle930) {
      attributes.named(LibraryElements::class.java, "aar")
    } else {
      objects.named(LibraryElements::class.java, "aar")
    }
    attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, libraryElements)
    return this
  }

  private fun HasConfigurableAttributes<*>.asJar(): HasConfigurableAttributes<*> {
    val libraryElements = if (isAtLeastGradle930) {
      attributes.named(LibraryElements::class.java, LibraryElements.JAR)
    } else {
      objects.named(LibraryElements::class.java, LibraryElements.JAR)
    }
    attributes.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, libraryElements)
    return this
  }

  private fun HasConfigurableAttributes<*>.preferAndroid(): HasConfigurableAttributes<*> {
    val targetJvmEnvironment = if (isAtLeastGradle930) {
      attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.ANDROID)
    } else {
      objects.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.ANDROID)
    }
    attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, targetJvmEnvironment)
    return this
  }

  private fun HasConfigurableAttributes<*>.preferStandardJVM(): HasConfigurableAttributes<*> {
    val targetJvmEnvironment = if (isAtLeastGradle930) {
      attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
    } else {
      objects.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
    }
    attributes.attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, targetJvmEnvironment)
    return this
  }
}
