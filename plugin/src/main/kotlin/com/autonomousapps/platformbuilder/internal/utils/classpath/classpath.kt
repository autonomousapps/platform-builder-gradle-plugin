// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.classpath

import com.autonomousapps.platformbuilder.PlatformBuilderPlugin

internal fun isAgpAvailable(): Boolean = checkClass("com.android.build.api.attributes.BuildTypeAttr")

internal fun isKgpAvailable(): Boolean = checkClass("org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType")

private fun checkClass(fqcn: String): Boolean {
  return try {
    val classLoader = PlatformBuilderPlugin::class.java.classLoader
    Class.forName(fqcn, false, classLoader)
    true
  } catch (_: ClassNotFoundException) {
    false
  }
}
