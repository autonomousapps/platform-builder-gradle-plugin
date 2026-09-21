// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import org.gradle.util.GradleVersion

internal class KmpFixture(
  gradleVersion: GradleVersion,
  private val hasKotlin: Boolean = true,
) : AbstractFixture(gradleVersion) {
  fun build(): GradleProject {
    return newGradleProjectBuilder()
      .withRootProject {
        if (!hasKotlin) {
          withSettingsScript {
            plugins(PLATFORM_BUILDER)
          }
        }
      }
      .withSubproject("platform") {
        withBuildScript {
          plugins(PLATFORM_BUILDER_PLUGIN)
          group = "com.example.platform"
          version = "0.1"

          val dependency = if (hasKotlin) {
            // KMP library with varied `KotlinPlatformType`s
            "androidx.compose.material:material-icons-core:1.7.8"
          } else {
            // something more boring
            "com.squareup.okhttp3:okhttp:5.5.0"
          }

          dependencies(
            platformApi(dependency),
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

  val expectedModuleFileContents = """
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
    |      "dependencies": [
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-bom",
    |          "version": {
    |            "requires": "1.7.1"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        }
    |      ],
    |      "dependencyConstraints": [
    |        {
    |          "group": "androidx.compose.material",
    |          "module": "material-icons-core",
    |          "version": {
    |            "requires": "1.7.8"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.material",
    |          "module": "material-icons-core-desktop",
    |          "version": {
    |            "requires": "1.7.8"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-saveable",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-geometry",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-graphics",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-text",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-unit",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-util",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-swing",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.skiko",
    |          "module": "skiko",
    |          "version": {
    |            "requires": "0.7.7"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-jvm",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-saveable-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-geometry-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-graphics-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-text-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-unit-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-util-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-core",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-jdk8",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.skiko",
    |          "module": "skiko-awt",
    |          "version": {
    |            "requires": "0.7.7"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-core-jvm",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-jdk7",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-common",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "23.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-desktop",
    |          "version": {
    |            "requires": "1.6.0"
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
    |      "dependencies": [
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-bom",
    |          "version": {
    |            "requires": "1.7.1"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        }
    |      ],
    |      "dependencyConstraints": [
    |        {
    |          "group": "androidx.compose.material",
    |          "module": "material-icons-core",
    |          "version": {
    |            "requires": "1.7.8"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.material",
    |          "module": "material-icons-core-desktop",
    |          "version": {
    |            "requires": "1.7.8"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-common",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains",
    |          "module": "annotations",
    |          "version": {
    |            "requires": "23.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-saveable",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-geometry",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-graphics",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-text",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-unit",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-util",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-jdk8",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-core",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-swing",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.skiko",
    |          "module": "skiko",
    |          "version": {
    |            "requires": "0.7.7"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-geometry-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-graphics-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-text-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-unit-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.ui",
    |          "module": "ui-util-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-jvm",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.compose.runtime",
    |          "module": "runtime-saveable-desktop",
    |          "version": {
    |            "requires": "1.6.0"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlin",
    |          "module": "kotlin-stdlib-jdk7",
    |          "version": {
    |            "requires": "1.8.20"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-core-jvm",
    |          "version": {
    |            "requires": "1.7.1"
    |          }
    |        },
    |        {
    |          "group": "org.jetbrains.skiko",
    |          "module": "skiko-awt",
    |          "version": {
    |            "requires": "0.7.7"
    |          }
    |        },
    |        {
    |          "group": "androidx.collection",
    |          "module": "collection",
    |          "version": {
    |            "requires": "1.4.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.collection",
    |          "module": "collection-jvm",
    |          "version": {
    |            "requires": "1.4.0"
    |          }
    |        }
    |      ]
    |    }
    |  ]
    |}
    |
  """.trimMargin()
}
