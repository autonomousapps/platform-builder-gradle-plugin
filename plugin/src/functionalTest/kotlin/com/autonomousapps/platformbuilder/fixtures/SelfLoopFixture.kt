// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import org.gradle.util.GradleVersion

internal class SelfLoopFixture(
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

  fun build(): GradleProject = newGradleProjectBuilder()
    .withPlatformBuilder(
      // The GMM for this dependency contains a self-constraint
      // "dependencyConstraints": [
      //   {
      //     "group": "androidx.media3",
      //     "module": "media3-common",
      //     "version": {
      //       "requires": "1.11.1"
      //     }
      //   }
      platformApi("androidx.media3:media3-common:1.11.1")
    )
    .write()

  fun expectedModuleFileContents(): String {
    return """
      {
        "formatVersion": "1.1",
        "component": {
          "group": "com.example.platform",
          "module": "platform",
          "version": "0.1",
          "attributes": {
            "org.gradle.status": "release"
          }
        },
        "createdBy": {
          "gradle": {
            "version": "${gradleVersion.version}"
          }
        },
        "variants": [
          {
            "name": "apiElements",
            "attributes": {
              "org.gradle.category": "platform",
              "org.gradle.usage": "java-api"
            },
            "dependencyConstraints": [
              {
                "group": "androidx.media3",
                "module": "media3-common",
                "version": {
                  "requires": "1.11.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-experimental",
                "version": {
                  "requires": "1.3.1"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "com.google.guava",
                "module": "failureaccess",
                "version": {
                  "requires": "1.0.2"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "1.7.10"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib-common",
                "version": {
                  "requires": "1.7.10"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "13.0"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              }
            ]
          },
          {
            "name": "runtimeElements",
            "attributes": {
              "org.gradle.category": "platform",
              "org.gradle.usage": "java-runtime"
            },
            "dependencyConstraints": [
              {
                "group": "androidx.media3",
                "module": "media3-common",
                "version": {
                  "requires": "1.11.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-experimental",
                "version": {
                  "requires": "1.3.1"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "com.google.guava",
                "module": "failureaccess",
                "version": {
                  "requires": "1.0.2"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "1.7.10"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib-common",
                "version": {
                  "requires": "1.7.10"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "13.0"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation",
                "version": {
                  "requires": "1.6.0"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-jvm",
                "version": {
                  "requires": "1.6.0"
                },
                "reason": "Required by androidx.media3:media3-common:1.11.1"
              }
            ]
          }
        ]
      }
    """.trimIndent()
  }
}
