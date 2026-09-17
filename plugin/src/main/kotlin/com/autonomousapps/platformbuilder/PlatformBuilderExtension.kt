// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import javax.inject.Inject

/**
 * *build.gradle.kts*
 * ```
 * platformBuilder {
 *   enablePublishing()
 * }
 * ```
 */
public abstract class PlatformBuilderExtension @Inject constructor(
  private val project: Project,
  objects: ObjectFactory,
) {

  // TODO: strict dependencies?
  internal val doPublish = objects.property(Boolean::class.java).convention(false)

  public fun enablePublishing() {
    doPublish.set(true)
    doPublish.disallowChanges()

    project.configurePublishing()
  }

  /**
   * @see <a href="https://docs.gradle.org/current/userguide/java_platform_plugin.html#sec:java_platform_publishing">Publishing platforms</a>
   */
  private fun Project.configurePublishing() {
    pluginManager.apply("maven-publish")
    extensions.configure(PublishingExtension::class.java) { publishing ->
      publishing.publications.run {
        create("platform", MavenPublication::class.java).run {
          from(components.getAt("javaPlatform"))
        }
      }
    }
  }

  internal companion object {
    const val NAME = "platformBuilder"

    fun create(project: Project): PlatformBuilderExtension {
      return project.extensions.create(NAME, PlatformBuilderExtension::class.java)
    }
  }
}
