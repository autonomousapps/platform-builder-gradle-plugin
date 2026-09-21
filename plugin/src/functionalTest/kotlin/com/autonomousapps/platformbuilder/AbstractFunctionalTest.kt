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
     * Fully-qualified method name to [gradleVersionsForAndroid], for use in non-static inner classes that are annotated
     * with `org.junit.jupiter.api.Nested`.
     *
     * @see <a
     *   href="https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-MethodSource">MethodSource</a>
     */
    const val GRADLE_VERSIONS_FOR_ANDROID: String =
      "com.autonomousapps.platformbuilder.AbstractFunctionalTest#gradleVersionsForAndroid"

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
      // TODO: define this somewhere else
      return setOf(GradleVersion.version("9.0.0"), GradleVersion.current())
    }

    /**
     * Use this with JUnit5 parameterized tests. For example:
     * ```
     * @ParameterizedTest
     * @MethodSource("gradleVersionsForAndroid")
     * fun `a functional test involving Android projects`(gradleVersion: GradleVersion) {}
     * ```
     *
     * This lets us easily test our plugins against multiple Gradle versions.
     *
     * @see <a href="https://gradle.org/release-candidate/">Gradle Release Candidate</a>
     */
    @JvmStatic
    protected fun gradleVersionsForAndroid(): Set<GradleVersion> {
      // TODO: define this somewhere else
      // min supported version of Gradle for AGP 9 is 9.1.0
      return setOf(GradleVersion.version("9.1.0"), GradleVersion.current())
    }
  }
}
