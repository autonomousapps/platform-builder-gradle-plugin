// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.provenance

import com.autonomousapps.platformbuilder.internal.model.ReasonedDependency
import com.autonomousapps.platformbuilder.internal.model.ReasonedDependencyConstraint
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.provider.Provider

// TODO fix or delete
internal fun Provider<Collection<ReasonedDependencyConstraint>>.withProvenanceForConstraints(
  apiConstraints: Provider<Collection<ReasonedDependencyConstraint>>
): Provider<List<DependencyConstraint>> {
  return flatMap { runtime ->
    apiConstraints.map { api ->
      runtime.map { r ->
        val reason = api.find { it.dependencyConstraint == r.dependencyConstraint }?.reason
        r.dependencyConstraint.apply { reason?.let { because(it) } }
      }
    }
  }
}

// TODO fix or delete
internal fun Provider<Collection<ReasonedDependency>>.withProvenanceForDependencies(
  apiDependencies: Provider<Collection<ReasonedDependency>>
): Provider<List<Dependency>> {
  return flatMap { runtime ->
    apiDependencies.map { api ->
      runtime.map { r ->
        val reason = api.find { it.dependency == r.dependency }?.reason
        r.dependency.apply { reason?.let { because(it) } }
      }
    }
  }
}
