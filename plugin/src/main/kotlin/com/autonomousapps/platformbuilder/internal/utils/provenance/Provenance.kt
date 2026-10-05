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

internal class Provenance(
  private val rootId: ComponentIdentifier,
  private val provenance: Graph<ComponentIdentifier>,
) {

  /*
   * Do all this once. Also, since this code is single-threaded, we don't require thread safety.
   */

  // Reverse the graph so we can see all nodes upstream of this node.
  private val reversedGraph by lazy(LazyThreadSafetyMode.NONE) { provenance.reversed() }
  private val directChildren by lazy(LazyThreadSafetyMode.NONE) { provenance.children(rootId) }
  private val theRoot by lazy(LazyThreadSafetyMode.NONE) { setOf(rootId) }

  fun buildReason(thisId: ComponentIdentifier): String = buildReason(computeDirectContributors(thisId))

  private fun buildReason(incomingEdges: Set<ComponentIdentifier>): String {
    return incomingEdges
      .mapTo(sortedSetOf()) { it.consistentDisplayName }
      .joinToString(prefix = "Required by ", separator = ", ")
  }

  private fun computeDirectContributors(thisId: ComponentIdentifier): Set<ComponentIdentifier> {
    // `thisId` is a directly declared constraint, so we return just the root as the provenance.
    if (thisId in directChildren) {
      return theRoot
    }

    // This will include the *root* node, which we only want to report if there is a direct edge connecting the root
    // node to this node.
    val reachable = reversedGraph.reachableNodes(thisId)
    // `directChildren` will not contain the root itself, so if the filter is empty, that's a signal that
    // this node is a direct child of the root node. That shouldn't be able to happen anymore, but :shrug:.
    return directChildren
      .filterTo(mutableSetOf()) { directChild -> directChild in reachable }
      // An empty set implies a direct child of the root
      .ifEmpty { theRoot }
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
