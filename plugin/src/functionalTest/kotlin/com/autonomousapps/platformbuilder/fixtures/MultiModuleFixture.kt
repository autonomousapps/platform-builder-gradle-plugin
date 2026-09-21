// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.gradle.Dependency.Companion.api
import com.autonomousapps.kit.gradle.Dependency.Companion.implementation
import com.autonomousapps.kit.gradle.android.AndroidBlock
import org.gradle.util.GradleVersion

internal class MultiModuleFixture(
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

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
            // Other platforms
            platformApi("androidx.compose:compose-bom:2026.08.00").onPlatform(),
            platformApi(":platform2").onPlatform(),
            // Java library (JAR)
            platformApi("com.squareup.okhttp3:okhttp:5.5.0"),
            // Android library (AAR)
            platformApi("androidx.viewpager2:viewpager2:1.1.0"),
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
            implementation("androidx.viewpager2:viewpager2"),
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
    |          "group": "androidx.viewpager2",
    |          "module": "viewpager2",
    |          "version": {
    |            "requires": "1.1.0"
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
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.2.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-experimental",
    |          "version": {
    |            "requires": "1.4.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.fragment",
    |          "module": "fragment",
    |          "version": {
    |            "requires": "1.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.recyclerview",
    |          "module": "recyclerview",
    |          "version": {
    |            "requires": "1.3.1"
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
    |          "group": "androidx.core",
    |          "module": "core",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.collection",
    |          "module": "collection",
    |          "version": {
    |            "requires": "1.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.viewpager",
    |          "module": "viewpager",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.loader",
    |          "module": "loader",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.activity",
    |          "module": "activity",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-viewmodel",
    |          "version": {
    |            "requires": "2.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.customview",
    |          "module": "customview",
    |          "version": {
    |            "requires": "1.0.0"
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
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-runtime",
    |          "version": {
    |            "requires": "2.3.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.versionedparcelable",
    |          "module": "versionedparcelable",
    |          "version": {
    |            "requires": "1.1.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-livedata",
    |          "version": {
    |            "requires": "2.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.savedstate",
    |          "module": "savedstate",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-common",
    |          "version": {
    |            "requires": "2.3.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.arch.core",
    |          "module": "core-common",
    |          "version": {
    |            "requires": "2.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.arch.core",
    |          "module": "core-runtime",
    |          "version": {
    |            "requires": "2.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-livedata-core",
    |          "version": {
    |            "requires": "2.0.0"
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
    |          "group": "androidx.viewpager2",
    |          "module": "viewpager2",
    |          "version": {
    |            "requires": "1.1.0"
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
    |          "group": "androidx.annotation",
    |          "module": "annotation",
    |          "version": {
    |            "requires": "1.2.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.annotation",
    |          "module": "annotation-experimental",
    |          "version": {
    |            "requires": "1.4.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.collection",
    |          "module": "collection",
    |          "version": {
    |            "requires": "1.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.core",
    |          "module": "core",
    |          "version": {
    |            "requires": "1.7.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.fragment",
    |          "module": "fragment",
    |          "version": {
    |            "requires": "1.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.recyclerview",
    |          "module": "recyclerview",
    |          "version": {
    |            "requires": "1.3.1"
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
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-runtime",
    |          "version": {
    |            "requires": "2.3.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.versionedparcelable",
    |          "module": "versionedparcelable",
    |          "version": {
    |            "requires": "1.1.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.viewpager",
    |          "module": "viewpager",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.loader",
    |          "module": "loader",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.activity",
    |          "module": "activity",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-viewmodel",
    |          "version": {
    |            "requires": "2.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.customview",
    |          "module": "customview",
    |          "version": {
    |            "requires": "1.0.0"
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
    |          "group": "androidx.arch.core",
    |          "module": "core-runtime",
    |          "version": {
    |            "requires": "2.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-common",
    |          "version": {
    |            "requires": "2.3.1"
    |          }
    |        },
    |        {
    |          "group": "androidx.arch.core",
    |          "module": "core-common",
    |          "version": {
    |            "requires": "2.1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-livedata",
    |          "version": {
    |            "requires": "2.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.savedstate",
    |          "module": "savedstate",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.lifecycle",
    |          "module": "lifecycle-livedata-core",
    |          "version": {
    |            "requires": "2.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.concurrent",
    |          "module": "concurrent-futures",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.customview",
    |          "module": "customview-poolingcontainer",
    |          "version": {
    |            "requires": "1.0.0"
    |          }
    |        },
    |        {
    |          "group": "com.google.guava",
    |          "module": "listenablefuture",
    |          "version": {
    |            "requires": "1.0"
    |          }
    |        },
    |        {
    |          "group": "androidx.core",
    |          "module": "core-ktx",
    |          "version": {
    |            "requires": "1.5.0"
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
    |          "group": "androidx.startup",
    |          "module": "startup-runtime",
    |          "version": {
    |            "requires": "1.2.0"
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
}
