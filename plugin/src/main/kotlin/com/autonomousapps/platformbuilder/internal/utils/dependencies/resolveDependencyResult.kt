// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.dependencies

import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.result.ResolvedDependencyResult

/**
 * A heuristic to determine if a dependency is requested with a rich version.
 *
 * @see <a href="https://docs.gradle.org/current/userguide/dependency_versions.html>Rich versions</a>
 */
internal fun ResolvedDependencyResult.isRichVersionRequest(): Boolean {
  val requested = requested as? ModuleComponentSelector ?: return false
  return ModuleVersion.of(requested.versionConstraint).isRichVersionRequest()
}

internal class ModuleVersion(
  val requiredVersion: String = "",
  val preferredVersion: String = "",
  val strictVersion: String = "",
) {

  fun isRichVersionRequest(): Boolean {
    val hasPreferredVersion = preferredVersion.isNotEmpty()
    val hasStrictVersion = strictVersion.isNotEmpty()

    return hasPreferredVersion
        || hasStrictVersion
        || RICH_SIGNALS.any { signal -> requiredVersion.contains(signal) }
  }

  companion object {
    /** @see <a href="https://docs.gradle.org/current/userguide/dependency_versions.html>Rich versions</a> */
    private val RICH_SIGNALS = setOf("[", "]", "(", ")", ",", "+", "latest.")

    fun of(versionConstraint: VersionConstraint): ModuleVersion {
      return ModuleVersion(
        requiredVersion = versionConstraint.requiredVersion,
        preferredVersion = versionConstraint.preferredVersion,
        strictVersion = versionConstraint.strictVersion,
      )
    }
  }
}
