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

import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.HasConfigurableAttributes
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.attributes.java.TargetJvmEnvironment

/*
 * These functions were borrowed and modified (to support Android from)
 * `org.gradle.api.plugins.jvm.internal.JvmPluginServices`.
 */

internal fun configureAsAndroidCompileClasspath(configuration: HasConfigurableAttributes<*>) {
  configuration.library().apiUsage().asAar().withExternalDependencies().preferAndroid()
}

internal fun configureAsAndroidRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
  configuration.library().runtimeUsage().asAar().withExternalDependencies().preferAndroid()
}

internal fun configureAsCompileClasspath(configuration: HasConfigurableAttributes<*>) {
  configuration.library().apiUsage().asJar().withExternalDependencies().preferStandardJVM()
}

internal fun configureAsRuntimeClasspath(configuration: HasConfigurableAttributes<*>) {
  configuration.library().runtimeUsage().asJar().withExternalDependencies().preferStandardJVM()
}

internal fun HasConfigurableAttributes<*>.library(): HasConfigurableAttributes<*> {
  attributes.attribute(Category.CATEGORY_ATTRIBUTE, attributes.named(Category::class.java, Category.LIBRARY))
  return this
}

internal fun HasConfigurableAttributes<*>.apiUsage(): HasConfigurableAttributes<*> {
  attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_API))
  return this
}

internal fun HasConfigurableAttributes<*>.runtimeUsage(): HasConfigurableAttributes<*> {
  attributes.attribute(Usage.USAGE_ATTRIBUTE, attributes.named(Usage::class.java, Usage.JAVA_RUNTIME))
  return this
}

internal fun HasConfigurableAttributes<*>.withExternalDependencies(): HasConfigurableAttributes<*> {
  attributes.attribute(Bundling.BUNDLING_ATTRIBUTE, attributes.named(Bundling::class.java, Bundling.EXTERNAL))
  return this
}

internal fun HasConfigurableAttributes<*>.asAar(): HasConfigurableAttributes<*> {
  attributes.attribute(
    LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
    attributes.named(LibraryElements::class.java, "aar")
  )
  return this
}

internal fun HasConfigurableAttributes<*>.asJar(): HasConfigurableAttributes<*> {
  attributes.attribute(
    LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
    attributes.named(LibraryElements::class.java, LibraryElements.JAR)
  )
  return this
}

internal fun HasConfigurableAttributes<*>.preferAndroid(): HasConfigurableAttributes<*> {
  attributes.attribute(
    TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
    attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.ANDROID)
  )
  return this
}

internal fun HasConfigurableAttributes<*>.preferStandardJVM(): HasConfigurableAttributes<*> {
  attributes.attribute(
    TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE,
    attributes.named(TargetJvmEnvironment::class.java, TargetJvmEnvironment.STANDARD_JVM)
  )
  return this
}
