// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.configurations

import com.autonomousapps.platformbuilder.internal.utils.versions.GradleVersions
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider

@Suppress("UnstableApiUsage")
internal class ConfigurationServices {

  private val isAtLeastGradle940 = GradleVersions.isAtLeast940

  fun extendsFrom(configuration: Configuration, vararg superConfigs: Provider<out Configuration>) {
    if (isAtLeastGradle940) {
      configuration.extendsFrom(*superConfigs)
    } else {
      configuration.extendsFrom(*superConfigs.map { it.get() }.toTypedArray())
    }
  }
}
