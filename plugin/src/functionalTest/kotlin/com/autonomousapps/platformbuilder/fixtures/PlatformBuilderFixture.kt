// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Dependency.Companion.api
import com.autonomousapps.kit.gradle.Dependency.Companion.implementation
import com.autonomousapps.kit.gradle.android.AndroidBlock

internal class PlatformBuilderFixture : AbstractFixture() {

  internal companion object {
    const val LIB_ANDROID_NAME = "libandroid"
    const val LIB_JAVA_NAME = "libjava"
    const val LIB_KOTLIN_NAME = "libkotlin"
  }

  fun build(): GradleProject {
    return newGradleProjectBuilder()
      .withSubproject("platform") {
        withBuildScript {
          plugins(PLATFORM_BUILDER_PLUGIN)
          group = "com.example.platform"
          version = "0.1"
          dependencies(
            platformApi("com.squareup.okhttp3:okhttp:5.5.0"),
            platformApi("androidx.compose:compose-bom:2026.08.00").onPlatform(),
            platformApi(":platform2").onPlatform(),
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
      .withSubproject("platform2") {
        withBuildScript {
          plugins(JAVA_PLATFORM)
          withGroovy(
            """
              javaPlatform {
                allowDependencies()
              }
              
              dependencies {
                api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.11.0"))
                
                // TODO(tsr): testkit-support needs a way to express constraints
                constraints {
                  api("org.apache.commons:commons-collections4:4.6.0")
                }
              }
            """.trimIndent()
          )
        }
      }
      .withAndroidLibProject(LIB_ANDROID_NAME) {
        withBuildScript {
          plugins(ANDROID_LIB)
          android = AndroidBlock(
            namespace = "com.example.$LIB_ANDROID_NAME",
            compileSdkVersion = COMPILE_SDK,
          )
          dependencies(
            api(":platform").onPlatform(),
            implementation("com.squareup.okhttp3:okhttp"),
            implementation("com.squareup.okio:okio"),
            // androidx.compose:compose-bom
            implementation("androidx.compose.animation:animation"),
          )
        }
      }
      .withSubproject(LIB_JAVA_NAME) {
        withBuildScript {
          plugins(JAVA_LIB)
          dependencies(
            api(":platform").onPlatform(),
            implementation("com.squareup.okhttp3:okhttp"),
            implementation("com.squareup.okio:okio"),
            implementation("org.apache.commons:commons-collections4"),
          )
        }
      }
      .withSubproject(LIB_KOTLIN_NAME) {
        withBuildScript {
          plugins(KOTLIN_JVM)
          dependencies(
            api(":platform").onPlatform(),
            implementation("com.squareup.okhttp3:okhttp"),
            implementation("com.squareup.okio:okio"),
            // org.jetbrains.kotlinx:kotlinx-coroutines-bom
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
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
    |      "version": "9.7.1"
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
    |          "group": "androidx.compose",
    |          "module": "compose-bom",
    |          "version": {
    |            "requires": "2026.08.00"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        },
    |        {
    |          "group": "the-project",
    |          "module": "platform2",
    |          "version": {
    |            "requires": "unspecified"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        }
    |      ],
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
    |          "group": "androidx.compose",
    |          "module": "compose-bom",
    |          "version": {
    |            "requires": "2026.08.00"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        },
    |        {
    |          "group": "the-project",
    |          "module": "platform2",
    |          "version": {
    |            "requires": "unspecified"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        },
    |        {
    |          "group": "org.jetbrains.kotlinx",
    |          "module": "kotlinx-coroutines-bom",
    |          "version": {
    |            "requires": "1.11.0"
    |          },
    |          "attributes": {
    |            "org.gradle.category": "platform"
    |          },
    |          "endorseStrictVersions": true
    |        }
    |      ],
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
    |        }
    |      ]
    |    }
    |  ]
    |}
    |""".trimMargin()
}
