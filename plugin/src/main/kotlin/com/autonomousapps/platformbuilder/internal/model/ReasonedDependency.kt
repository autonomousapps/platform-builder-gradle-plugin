// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.model

import org.gradle.api.artifacts.Dependency

internal class ReasonedDependency(
  val dependency: Dependency,
  val reason: String,
)
