// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.model

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint

internal class GetDependenciesResult(
  val constraints: Collection<DependencyConstraint>,
  val dependencies: Collection<Dependency>,
)
