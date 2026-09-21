// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.versions

import org.gradle.util.GradleVersion

internal object GradleVersions {
  private val current = GradleVersion.current()
  private val gradle930 = GradleVersion.version("9.3.0")
  private val gradle940 = GradleVersion.version("9.4.0")

  private val gradle950 = GradleVersion.version("9.5.0")

  val isAtLeast930 = current >= gradle930
  val isAtLeast940 = current >= gradle940
  val isAtLeast950 = current >= gradle950
}
