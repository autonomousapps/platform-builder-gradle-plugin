// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
@file:Suppress("UnstableApiUsage") // Guava Graph

package com.autonomousapps.platformbuilder.internal.utils.provenance

import com.autonomousapps.graph.Graphs.children
import com.autonomousapps.graph.Graphs.reachableNodes
import com.autonomousapps.graph.Graphs.reversed
import com.google.common.graph.Graph
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier

internal fun buildReason(
  rootId: ComponentIdentifier,
  thisId: ComponentIdentifier,
  provenance: Graph<ComponentIdentifier>,
): String = buildReason(computeDirectContributors(rootId = rootId, thisId = thisId, provenance = provenance))

private fun buildReason(incomingEdges: Set<ComponentIdentifier>): String {
  return incomingEdges
    .mapTo(sortedSetOf()) { it.consistentDisplayName }
    .joinToString(prefix = "Required by ", separator = ", ")
}

private fun computeDirectContributors(
  rootId: ComponentIdentifier,
  thisId: ComponentIdentifier,
  provenance: Graph<ComponentIdentifier>,
): Set<ComponentIdentifier> {
  // TODO: doing this would avoid the cost of reversing the graph for direct edges from the root. Is that actually
  //  expensive? If not expensive, not sure it's worth complexifying this code.
  //   val directChildren = provenance.children(rootId)
  //   if (directChildren.contains(thisId)) {
  //
  //   }

  // Reverse the graph so we can see all nodes upstream of this node.
  val reversed = provenance.reversed()
  // This will include the *root* node, which we only want to report if there is a direct edge connecting the root node
  // to this node.
  val reachable = reversed.reachableNodes(thisId)
  // `provenance.children(rootId)` will not contain the root itself, so if the filter is empty, that's our signal that
  // this node is a direct child of the root node.
  return provenance.children(rootId)
    .filterTo(linkedSetOf()) { child -> child in reachable }
    // An empty set implies a direct child of the root
    .ifEmpty { setOf(rootId) }
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
