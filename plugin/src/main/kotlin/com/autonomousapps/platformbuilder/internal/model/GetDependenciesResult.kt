// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.model

internal class GetDependenciesResult(
  val constraints: Collection<ReasonedDependencyConstraint>,
  val dependencies: Collection<ReasonedDependency>,
)
