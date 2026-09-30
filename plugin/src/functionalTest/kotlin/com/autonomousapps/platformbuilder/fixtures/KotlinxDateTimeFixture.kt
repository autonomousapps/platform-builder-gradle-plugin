// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.platformbuilder.fixtures.HasGuavaFixture.Dependency
import org.gradle.util.GradleVersion

//square-okio-fakefilesystem = { module = "com.squareup.okio:okio-fakefilesystem", version.ref = "square-okio" }
internal class KotlinxDateTimeFixture(
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

  fun build(): GradleProject {
    return newGradleProjectBuilder()
      .withSubproject("platform") {
        withBuildScript {
          plugins(PLATFORM_BUILDER_PLUGIN)
          group = "com.example.platform"
          version = "0.1"

          dependencies(
            // brings in datetime:0.8.0-0.6.0-compat
            platformApi("com.squareup.okio:okio-fakefilesystem:3.18.2"),
            platformApi("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1"),
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
    return """
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
      |          "group": "com.squareup.okio",
      |          "module": "okio-fakefilesystem",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "project :platform"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlinx",
      |          "module": "kotlinx-datetime",
      |          "version": {
      |            "requires": "0.8.0-0.6.x-compat"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem-jvm:3.18.2"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio-fakefilesystem-jvm",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem:3.18.2"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlinx",
      |          "module": "kotlinx-datetime-jvm",
      |          "version": {
      |            "requires": "0.8.0-0.6.x-compat"
      |          },
      |          "reason": "org.jetbrains.kotlinx:kotlinx-datetime:0.8.0-0.6.x-compat"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlin",
      |          "module": "kotlin-stdlib",
      |          "version": {
      |            "requires": "2.1.21"
      |          },
      |          "reason": "com.squareup.okio:okio-jvm:3.18.2"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem-jvm:3.18.2"
      |        },
      |        {
      |          "group": "org.jetbrains",
      |          "module": "annotations",
      |          "version": {
      |            "requires": "13.0"
      |          },
      |          "reason": "org.jetbrains.kotlin:kotlin-stdlib:2.1.21"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio-jvm",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio:3.18.2"
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
      |          "group": "com.squareup.okio",
      |          "module": "okio-fakefilesystem",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "project :platform"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlinx",
      |          "module": "kotlinx-datetime",
      |          "version": {
      |            "requires": "0.8.0-0.6.x-compat"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem-jvm:3.18.2"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio-fakefilesystem-jvm",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem:3.18.2"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlinx",
      |          "module": "kotlinx-datetime-jvm",
      |          "version": {
      |            "requires": "0.8.0-0.6.x-compat"
      |          },
      |          "reason": "org.jetbrains.kotlinx:kotlinx-datetime:0.8.0-0.6.x-compat"
      |        },
      |        {
      |          "group": "org.jetbrains.kotlin",
      |          "module": "kotlin-stdlib",
      |          "version": {
      |            "requires": "2.1.21"
      |          },
      |          "reason": "com.squareup.okio:okio-jvm:3.18.2"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio-fakefilesystem-jvm:3.18.2"
      |        },
      |        {
      |          "group": "org.jetbrains",
      |          "module": "annotations",
      |          "version": {
      |            "requires": "13.0"
      |          },
      |          "reason": "org.jetbrains.kotlin:kotlin-stdlib:2.1.21"
      |        },
      |        {
      |          "group": "com.squareup.okio",
      |          "module": "okio-jvm",
      |          "version": {
      |            "requires": "3.18.2"
      |          },
      |          "reason": "com.squareup.okio:okio:3.18.2"
      |        }
      |      ]
      |    }
      |  ]
      |}
      |
    """.trimMargin()
  }
}
