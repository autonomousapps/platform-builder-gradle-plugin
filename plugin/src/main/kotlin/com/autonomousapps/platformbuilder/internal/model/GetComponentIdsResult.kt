// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.model

import com.google.common.graph.Graph
import org.gradle.api.artifacts.component.ComponentIdentifier

@Suppress("UnstableApiUsage") // Guava Graph
internal class GetComponentIdsResult(
  val regularComponents: Set<ComponentIdentifier>,
  val platformComponents: Set<ComponentIdentifier>,
  val provenance: Graph<ComponentIdentifier>,
)
