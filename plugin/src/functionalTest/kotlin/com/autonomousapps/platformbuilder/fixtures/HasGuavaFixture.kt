// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import org.gradle.util.GradleVersion

internal class HasGuavaFixture(
  private val dependency: Dependency,
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

  enum class Dependency {
    ANDROID,
    JRE,
    LISTENABLEFUTURE_1,
    LISTENABLEFUTURE_9999
  }

  fun build(): GradleProject {
    return newGradleProjectBuilder()
      .withSubproject("platform") {
        withBuildScript {
          plugins(PLATFORM_BUILDER_PLUGIN)
          group = "com.example.platform"
          version = "0.1"

          val guava = when (dependency) {
            Dependency.ANDROID -> "com.google.guava:guava:33.5.0-android"
            Dependency.JRE -> "com.google.guava:guava:33.5.0-jre"
            Dependency.LISTENABLEFUTURE_1 -> "com.google.guava:listenablefuture:1.0"
            Dependency.LISTENABLEFUTURE_9999 -> "com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava"
          }

          dependencies(
            platformApi(guava),
            platformApi("com.squareup.okhttp3:okhttp:5.5.0"),
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
      .write()
  }

  fun expectedModuleFileContents(): String {
    return when (dependency) {
      Dependency.ANDROID -> expectedAndroidContents
      Dependency.JRE -> expectedJreContents
      Dependency.LISTENABLEFUTURE_1 -> expectedListenable1Contents
      Dependency.LISTENABLEFUTURE_9999 -> expectedListenable9999Contents
    }
  }

  private val expectedAndroidContents = """
    |{
    |  "formatVersion": "1.1",
    |  "component": {
    |    "group": "com.example.platform",
    |    "module": "platform",
    |    "version": "0.1",
    |    "attributes": {
    |      "org.gradle.status": "release"
    |    }
    |  },
    |  "createdBy": {
    |    "gradle": {
    |      "version": "${gradleVersion.version}"
    |    }
    |  },
    |  "variants": [
    |    {
    |      "name": "apiElements",
    |      "attributes": {
    |        "org.gradle.category": "platform",
    |        "org.gradle.usage": "java-api"
    |      },
    |      "dependencyConstraints": [
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.guava",
    |          "module": "failureaccess",
    |          "version": {
    |            "requires": "1.0.3"
    |          }
    |        },
    |        {
    |          "group": "org.jspecify",
    |          "module": "jspecify",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.errorprone",
    |          "module": "error_prone_annotations",
    |          "version": {
    |            "requires": "2.41.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.j2objc",
    |          "module": "j2objc-annotations",
    |          "version": {
    |            "requires": "3.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-jvm",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "2.2.21"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "13.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio-jvm",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-android",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        }
    |      ]
    |    },
    |    {
    |      "name": "runtimeElements",
    |      "attributes": {
    |        "org.gradle.category": "platform",
    |        "org.gradle.usage": "java-runtime"
    |      },
    |      "dependencyConstraints": [
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.guava",
    |          "module": "failureaccess",
    |          "version": {
    |            "requires": "1.0.3"
    |          }
    |        },
    |        {
    |          "group": "org.jspecify",
    |          "module": "jspecify",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.errorprone",
    |          "module": "error_prone_annotations",
    |          "version": {
    |            "requires": "2.41.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.j2objc",
    |          "module": "j2objc-annotations",
    |          "version": {
    |            "requires": "3.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-jvm",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "2.2.21"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "13.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio-jvm",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-android",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.10.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.startup",
    |          "module": "startup-runtime",
    |          "version": {
    |            "requires": "1.2.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-jvm",
    |          "version": {
    |            "requires": "1.10.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.tracing",
    |          "module": "tracing",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        }
    |      ]
    |    }
    |  ]
    |}
    |""".trimMargin()

  private val expectedJreContents = expectedAndroidContents

  private val expectedListenable1Contents = """
    |{
    |  "formatVersion": "1.1",
    |  "component": {
    |    "group": "com.example.platform",
    |    "module": "platform",
    |    "version": "0.1",
    |    "attributes": {
    |      "org.gradle.status": "release"
    |    }
    |  },
    |  "createdBy": {
    |    "gradle": {
    |      "version": "${gradleVersion.version}"
    |    }
    |  },
    |  "variants": [
    |    {
    |      "name": "apiElements",
    |      "attributes": {
    |        "org.gradle.category": "platform",
    |        "org.gradle.usage": "java-api"
    |      },
    |      "dependencyConstraints": [
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-jvm",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "2.2.21"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "13.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio-jvm",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-android",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        }
    |      ]
    |    },
    |    {
    |      "name": "runtimeElements",
    |      "attributes": {
    |        "org.gradle.category": "platform",
    |        "org.gradle.usage": "java-runtime"
    |      },
    |      "dependencyConstraints": [
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-jvm",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "2.2.21"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "13.0"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okio",
    |          "module": "okio-jvm",
    |          "version": {
    |            "requires": "3.18.1"
    |          }
    |        },
    |        {
    |          "group": "com.squareup.okhttp3",
    |          "module": "okhttp-android",
    |          "version": {
    |            "requires": "5.5.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.10.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.startup",
    |          "module": "startup-runtime",
    |          "version": {
    |            "requires": "1.2.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-jvm",
    |          "version": {
    |            "requires": "1.10.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.tracing",
    |          "module": "tracing",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        }
    |      ]
    |    }
    |  ]
    |}
    |""".trimMargin()

  private val expectedListenable9999Contents = expectedListenable1Contents
}
