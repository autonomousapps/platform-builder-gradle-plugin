// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Dependency.Companion.implementation
import com.autonomousapps.kit.gradle.android.AndroidBlock
import org.gradle.util.GradleVersion

internal class MultipleIncomingEdgesFixture(
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

  // note that this being `implementation` means the platform will treat this as _runtime constraints_
  private val okio = implementation("com.squareup.okio:okio:3.18.2")

  fun build(): GradleProject {
    return newGradleProjectBuilder()
      .withSubproject("platform") {
        withBuildScript {
          plugins(PLATFORM_BUILDER_PLUGIN)
          group = "com.example.platform"
          version = "0.1"

          dependencies(
            platformApi(":producer-java"),
            platformApi(":producer-kotlin"),
            platformApi(":producer-android"),
          )
          withGroovy(
            """
              platformBuilder {
                enablePublishing()
              }
              
              publishing {
                repositories {
                  maven {
                    name = "test"
                    url = uri(layout.buildDirectory.dir("repo"))
                  }
                }
              }
            """.trimIndent()
          )
        }
      }
      // These three libs all have the same dependency on okio, so they should all show up as okio's provenance in the
      // platform.
      .withSubproject("producer-java") {
        withBuildScript {
          group = "com.example.platform"
          version = "0.1"

          plugins(JAVA_LIB)
          dependencies(okio)
        }
      }
      .withSubproject("producer-kotlin") {
        withBuildScript {
          group = "com.example.platform"
          version = "0.1"

          plugins(KOTLIN_JVM)
          dependencies(okio)
        }
      }
      .withSubproject("producer-android") {
        withBuildScript {
          group = "com.example.platform"
          version = "0.1"

          plugins(ANDROID_LIB)
          dependencies(okio)
          android = AndroidBlock(
            namespace = "com.producer.android",
            compileSdkVersion = COMPILE_SDK,
          )
        }
      }
      .write()
  }

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
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "2.4.10"
                },
                "reason": "Required by project ':producer-android', project ':producer-kotlin'"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "13.0"
                },
                "reason": "Required by project ':producer-android', project ':producer-kotlin'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
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
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "2.4.10"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "13.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.squareup.okio",
                "module": "okio",
                "version": {
                  "requires": "3.18.2"
                },
                "reason": "Required by project ':producer-android', project ':producer-java', project ':producer-kotlin'"
              },
              {
                "group": "com.squareup.okio",
                "module": "okio-jvm",
                "version": {
                  "requires": "3.18.2"
                },
                "reason": "Required by project ':producer-android', project ':producer-java', project ':producer-kotlin'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-java",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-kotlin",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.example.platform",
                "module": "producer-android",
                "version": {
                  "requires": "0.1"
                },
                "reason": "Required by project ':platform'"
              }
            ]
          }
        ]
      }
    """.trimIndent()
  }
}
