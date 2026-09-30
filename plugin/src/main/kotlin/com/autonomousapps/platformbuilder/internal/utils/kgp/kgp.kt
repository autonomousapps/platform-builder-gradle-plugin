// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.kgp

import com.autonomousapps.platformbuilder.PlatformBuilderPlugin

internal fun isKgpAvailable(): Boolean {
  return try {
    val classLoader = PlatformBuilderPlugin::class.java.classLoader
    Class.forName("org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType", false, classLoader)
    true
  } catch (_: ClassNotFoundException) {
    false
  }
}
