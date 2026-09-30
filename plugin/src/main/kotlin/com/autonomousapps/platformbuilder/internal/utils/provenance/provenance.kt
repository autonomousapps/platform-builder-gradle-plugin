// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.provenance

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

internal fun MutableMap<ComponentIdentifier, MutableSet<ComponentIdentifier>>.putReason(
  selectedComponent: ComponentIdentifier,
  incomingEdge: ComponentIdentifier,
) {
  merge(selectedComponent, mutableSetOf(incomingEdge)) { acc, inc ->
    acc.apply { addAll(inc) }
  }
}

internal fun DependencyConstraint.declareProvenance(
  componentId: ComponentIdentifier,
  provenance: Map<ComponentIdentifier, Set<ComponentIdentifier>>,
): DependencyConstraint {
  val incomingEdges = provenance[componentId] ?: return this
  because(buildReason(incomingEdges))
  return this
}

internal fun Dependency.declareProvenance(
  componentId: ComponentIdentifier,
  provenance: Map<ComponentIdentifier, Set<ComponentIdentifier>>,
): Dependency {
  val incomingEdges = provenance[componentId] ?: return this
  because(buildReason(incomingEdges))
  return this
}

private fun buildReason(incomingEdges: Set<ComponentIdentifier>): String {
  return incomingEdges.joinToString(prefix = "Required by: ", separator = ", ") {
    "'${it.consistentDisplayName}'"
  }
}

/**
 * Between Gradle 9.0 and 9.7, the value of `ProjectComponentIdentifier.displayName` changed. It complicates tests, so
 * we just force it to the latter version.
 */
private val ComponentIdentifier.consistentDisplayName: String
  get() {
    return when (this) {
      is ProjectComponentIdentifier -> "project '${projectPath}'"
      // could add more later if it seems useful
      else -> displayName
    }
  }
