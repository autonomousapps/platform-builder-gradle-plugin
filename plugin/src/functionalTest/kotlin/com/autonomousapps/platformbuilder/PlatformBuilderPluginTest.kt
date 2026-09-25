// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder

import com.autonomousapps.kit.GradleBuilder.build
import com.autonomousapps.platformbuilder.fixtures.HasGuavaFixture
import com.autonomousapps.platformbuilder.fixtures.KmpFixture
import com.autonomousapps.platformbuilder.fixtures.MultiModuleFixture
import org.assertj.core.api.Assertions.assertThat
import org.gradle.util.GradleVersion
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.io.path.readText

internal class PlatformBuilderPluginTest : AbstractFunctionalTest() {

  @ParameterizedTest(name = "{0}")
  @MethodSource("gradleVersionsForAndroid")
  fun `consuming project gets versions from the graph resolved by the platform`(gradleVersion: GradleVersion) {
    // Given
    val fixture = MultiModuleFixture(gradleVersion)
    val gradleProject = fixture.build()

    // When (Android)
    var dependencies = ":${MultiModuleFixture.LIB_ANDROID_NAME}:dependencies"
    var result = build(gradleVersion, gradleProject.rootDir, dependencies, "--configuration", "debugRuntimeClasspath")

    // Then
    var output = result.output
    assertThat(output).contains("com.squareup.okhttp3:okhttp -> 5.5.0")
    assertThat(output).contains("com.squareup.okio:okio -> 3.18.1")
    assertThat(output).contains("androidx.compose.animation:animation -> 1.12.0")
    assertThat(output).contains("androidx.viewpager2:viewpager2:1.1.0-beta02 -> 1.1.0 (c)")

    // When (Java)
    dependencies = ":${MultiModuleFixture.LIB_JAVA_NAME}:dependencies"
    result = build(gradleVersion, gradleProject.rootDir, dependencies, "--configuration", "runtimeClasspath")

    // Then
    output = result.output
    assertThat(output).contains("com.squareup.okhttp3:okhttp -> 5.5.0")
    assertThat(output).contains("com.squareup.okio:okio -> 3.18.1")
    assertThat(output).contains("org.apache.commons:commons-collections4 -> 4.6.0")

    // When (Kotlin)
    dependencies = ":${MultiModuleFixture.LIB_KOTLIN_NAME}:dependencies"
    result = build(gradleVersion, gradleProject.rootDir, dependencies, "--configuration", "runtimeClasspath")

    // Then
    output = result.output
    assertThat(output).contains("com.squareup.okhttp3:okhttp -> 5.5.0")
    assertThat(output).contains("com.squareup.okio:okio -> 3.18.1")
    assertThat(output).contains("org.jetbrains.kotlinx:kotlinx-coroutines-core -> 1.11.0")
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("gradleVersionsForAndroid")
  fun `can publish platform`(gradleVersion: GradleVersion) {
    // Given
    val fixture = MultiModuleFixture(gradleVersion)
    val gradleProject = fixture.build()

    // When
    build(gradleVersion, gradleProject.rootDir, ":platform:publishPlatformPublicationToTestRepository")

    // Then
    val repo = gradleProject.singleArtifact("platform", "repo/com/example/platform/platform/0.1")
    with(repo.asPath) {
      assertThat(this).exists().isDirectory()
      assertThat(resolve("platform-0.1.pom")).exists().isRegularFile()

      val module = resolve("platform-0.1.module")
      assertThat(module).exists().isRegularFile()
      assertThat(module.readText()).isEqualTo(fixture.expectedModuleFileContents)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("gradleVersions")
  fun `can publish platform with KMP constraints`(gradleVersion: GradleVersion) {
    // Given
    val fixture = KmpFixture(gradleVersion)
    val gradleProject = fixture.build()

    // When
    build(gradleVersion, gradleProject.rootDir, ":platform:publishPlatformPublicationToTestRepository")

    // Then
    val repo = gradleProject.singleArtifact("platform", "repo/com/example/platform/platform/0.1")
    with(repo.asPath) {
      assertThat(this).exists().isDirectory()
      assertThat(resolve("platform-0.1.pom")).exists().isRegularFile()

      val module = resolve("platform-0.1.module")
      assertThat(module).exists().isRegularFile()
      assertThat(module.readText()).isEqualTo(fixture.expectedModuleFileContents)
    }
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("gradleVersions")
  fun `build succeeds when KGP not present`(gradleVersion: GradleVersion) {
    // Given
    val fixture = KmpFixture(gradleVersion, hasKotlin = false)
    val gradleProject = fixture.build()

    // When
    build(gradleVersion, gradleProject.rootDir, ":platform:publishPlatformPublicationToTestRepository")
  }

  @ParameterizedTest(name = "{0}, {1}")
  @MethodSource("gradlePlusGuava")
  fun `refuses to include constraint on guava or listenablefuture`(
    gradleVersion: GradleVersion,
    dependency: HasGuavaFixture.Dependency,
  ) {
    // Given
    val fixture = HasGuavaFixture(dependency, gradleVersion)
    val gradleProject = fixture.build()

    // When
    build(gradleVersion, gradleProject.rootDir, ":platform:publishPlatformPublicationToTestRepository")

    // Then
    val repo = gradleProject.singleArtifact("platform", "repo/com/example/platform/platform/0.1")
    with(repo.asPath) {
      assertThat(this).exists().isDirectory()
      assertThat(resolve("platform-0.1.pom")).exists().isRegularFile()

      val module = resolve("platform-0.1.module")
      assertThat(module).exists().isRegularFile()
      assertThat(module.readText()).isEqualTo(fixture.expectedModuleFileContents())
    }
  }

  private companion object {
    @JvmStatic
    fun gradlePlusGuava(): Stream<Arguments> {
      return HasGuavaFixture.Dependency.entries
        .flatMap { d -> gradleVersions().map { g -> g to d } }
        .map { (gradleVersion, dependency) -> Arguments.of(gradleVersion, dependency) }
        .stream()
    }
  }
}
