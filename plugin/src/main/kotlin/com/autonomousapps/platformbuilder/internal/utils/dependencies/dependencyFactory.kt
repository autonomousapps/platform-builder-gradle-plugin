// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.dependencies

import com.autonomousapps.platformbuilder.internal.utils.versions.GradleVersions
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

internal fun Project.newProjectDependency(path: String): ProjectDependency {
  return if (GradleVersions.isAtLeast950) {
    dependencyFactory.createProjectDependency(path)
  } else {
    val p = requireNotNull(findProject(path)) { "No project with path '$path'." }
    @Suppress("DEPRECATION")
    dependencyFactory.create(p)
  }
}
