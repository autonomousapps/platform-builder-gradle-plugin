// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder

import org.gradle.util.GradleVersion

internal abstract class AbstractFunctionalTest {
  protected companion object {
    /**
     * Fully-qualified method name to [gradleVersions], for use in non-static inner classes that are annotated with
     * `org.junit.jupiter.api.Nested`.
     *
     * @see <a
     *   href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-MethodSource">MethodSource</a>
     */
    const val GRADLE_VERSIONS: String = "com.autonomousapps.platformbuilder.AbstractFunctionalTest#gradleVersions"

    /**
     * Use this with JUnit5 parameterized tests. For example:
     * ```
     * @ParameterizedTest
     * @MethodSource("gradleVersions")
     * fun `a functional test`(gradleVersion: GradleVersion) {}
     * ```
     *
     * This lets us easily test our plugins against multiple Gradle versions.
     *
     * @see <a href="https://gradle.org/release-candidate/">Gradle Release Candidate</a>
     */
    @JvmStatic
    protected fun gradleVersions(): Set<GradleVersion> {
      // TODO: add more supported versions of Gradle
      return setOf(GradleVersion.current())
    }
  }
}
