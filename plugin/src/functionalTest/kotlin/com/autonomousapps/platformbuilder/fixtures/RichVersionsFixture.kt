// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.fixtures

import com.autonomousapps.kit.GradleProject
import org.gradle.util.GradleVersion

internal class RichVersionsFixture(
  gradleVersion: GradleVersion,
) : AbstractFixture(gradleVersion) {

  fun build(): GradleProject = newGradleProjectBuilder()
    .withPlatformBuilder(
      // com.google.ads.mediation:ironsource:9.3.0.1
      // \-- com.unity3d.ads-mediation:mediation-sdk:9.3.0
      //     \-- com.unity3d.ads-mediation:adquality-sdk:[9.2.1,9.3.0)
      platformApi("com.google.ads.mediation:ironsource:9.3.0.1")
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
            "dependencies": [
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-bom",
                "version": {
                  "requires": "1.8.0"
                },
                "attributes": {
                  "org.gradle.category": "platform"
                },
                "endorseStrictVersions": true,
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              }
            ],
            "dependencyConstraints": [
              {
                "group": "com.google.ads.mediation",
                "module": "ironsource",
                "version": {
                  "requires": "9.3.0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.unity3d.ads-mediation",
                "module": "mediation-sdk",
                "version": {
                  "requires": "9.3.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation",
                "version": {
                  "requires": "1.8.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads",
                "version": {
                  "requires": "24.9.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "2.1.10"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.unity3d.ads-mediation",
                "module": "adquality-sdk",
                "version": {
                  "requires": "9.2.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-jvm",
                "version": {
                  "requires": "1.8.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.browser",
                "module": "browser",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.collection",
                "module": "collection",
                "version": {
                  "requires": "1.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.core",
                "module": "core",
                "version": {
                  "requires": "1.10.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.datastore",
                "module": "datastore",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.privacysandbox.ads",
                "module": "ads-adservices",
                "version": {
                  "requires": "1.0.0-beta05"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.privacysandbox.ads",
                "module": "ads-adservices-java",
                "version": {
                  "requires": "1.0.0-beta05"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.webkit",
                "module": "webkit",
                "version": {
                  "requires": "1.12.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads-api",
                "version": {
                  "requires": "24.9.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads-identifier",
                "version": {
                  "requires": "18.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-appset",
                "version": {
                  "requires": "16.0.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-basement",
                "version": {
                  "requires": "18.9.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-tasks",
                "version": {
                  "requires": "18.2.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-android",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-core",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "23.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-experimental",
                "version": {
                  "requires": "1.4.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-runtime",
                "version": {
                  "requires": "2.3.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.versionedparcelable",
                "module": "versionedparcelable",
                "version": {
                  "requires": "1.1.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.datastore",
                "module": "datastore-core",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.work",
                "module": "work-runtime",
                "version": {
                  "requires": "2.7.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-measurement-sdk-api",
                "version": {
                  "requires": "20.1.2"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.ump",
                "module": "user-messaging-platform",
                "version": {
                  "requires": "3.2.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-base",
                "version": {
                  "requires": "18.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.fragment",
                "module": "fragment",
                "version": {
                  "requires": "1.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-core-jvm",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-common",
                "version": {
                  "requires": "2.3.1"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.arch.core",
                "module": "core-common",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-livedata",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.startup",
                "module": "startup-runtime",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-measurement-base",
                "version": {
                  "requires": "20.1.2"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.viewpager",
                "module": "viewpager",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.loader",
                "module": "loader",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.activity",
                "module": "activity",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-viewmodel",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.arch.core",
                "module": "core-runtime",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-livedata-core",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.customview",
                "module": "customview",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              },
              {
                "group": "androidx.savedstate",
                "module": "savedstate",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              }
            ]
          },
          {
            "name": "runtimeElements",
            "attributes": {
              "org.gradle.category": "platform",
              "org.gradle.usage": "java-runtime"
            },
            "dependencies": [
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-bom",
                "version": {
                  "requires": "1.8.0"
                },
                "attributes": {
                  "org.gradle.category": "platform"
                },
                "endorseStrictVersions": true,
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-bom",
                "version": {
                  "requires": "1.8.0"
                },
                "attributes": {
                  "org.gradle.category": "platform"
                },
                "endorseStrictVersions": true,
                "reason": "Required by com.google.ads.mediation:ironsource:9.3.0.1"
              }
            ],
            "dependencyConstraints": [
              {
                "group": "com.google.ads.mediation",
                "module": "ironsource",
                "version": {
                  "requires": "9.3.0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.unity3d.ads-mediation",
                "module": "mediation-sdk",
                "version": {
                  "requires": "9.3.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation",
                "version": {
                  "requires": "1.8.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads",
                "version": {
                  "requires": "24.9.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlin",
                "module": "kotlin-stdlib",
                "version": {
                  "requires": "2.1.10"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.unity3d.ads-mediation",
                "module": "adquality-sdk",
                "version": {
                  "requires": "9.2.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-jvm",
                "version": {
                  "requires": "1.8.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.browser",
                "module": "browser",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.collection",
                "module": "collection",
                "version": {
                  "requires": "1.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.core",
                "module": "core",
                "version": {
                  "requires": "1.10.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.datastore",
                "module": "datastore",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.privacysandbox.ads",
                "module": "ads-adservices",
                "version": {
                  "requires": "1.0.0-beta05"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.privacysandbox.ads",
                "module": "ads-adservices-java",
                "version": {
                  "requires": "1.0.0-beta05"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.webkit",
                "module": "webkit",
                "version": {
                  "requires": "1.12.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads-api",
                "version": {
                  "requires": "24.9.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-ads-identifier",
                "version": {
                  "requires": "18.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-appset",
                "version": {
                  "requires": "16.0.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-basement",
                "version": {
                  "requires": "18.9.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-tasks",
                "version": {
                  "requires": "18.2.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-android",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-core",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains",
                "module": "annotations",
                "version": {
                  "requires": "23.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.annotation",
                "module": "annotation-experimental",
                "version": {
                  "requires": "1.4.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-runtime",
                "version": {
                  "requires": "2.3.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.versionedparcelable",
                "module": "versionedparcelable",
                "version": {
                  "requires": "1.1.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.datastore",
                "module": "datastore-core",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.work",
                "module": "work-runtime",
                "version": {
                  "requires": "2.7.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-measurement-sdk-api",
                "version": {
                  "requires": "20.1.2"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.ump",
                "module": "user-messaging-platform",
                "version": {
                  "requires": "3.2.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-base",
                "version": {
                  "requires": "18.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.fragment",
                "module": "fragment",
                "version": {
                  "requires": "1.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "org.jetbrains.kotlinx",
                "module": "kotlinx-coroutines-core-jvm",
                "version": {
                  "requires": "1.8.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.arch.core",
                "module": "core-runtime",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-common",
                "version": {
                  "requires": "2.3.1"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.arch.core",
                "module": "core-common",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-livedata",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.startup",
                "module": "startup-runtime",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "com.google.android.gms",
                "module": "play-services-measurement-base",
                "version": {
                  "requires": "20.1.2"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.viewpager",
                "module": "viewpager",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.loader",
                "module": "loader",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.activity",
                "module": "activity",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-viewmodel",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-livedata-core",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.customview",
                "module": "customview",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.savedstate",
                "module": "savedstate",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by project ':platform'"
              },
              {
                "group": "androidx.concurrent",
                "module": "concurrent-futures",
                "version": {
                  "requires": "1.1.0"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.interpolator",
                "module": "interpolator",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by androidx.browser:browser:1.8.0, androidx.core:core:1.10.1, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.core",
                "module": "core-ktx",
                "version": {
                  "requires": "1.10.1"
                },
                "reason": "Required by androidx.core:core:1.10.1, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.room",
                "module": "room-runtime",
                "version": {
                  "requires": "2.2.5"
                },
                "reason": "Required by androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.sqlite",
                "module": "sqlite",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.sqlite",
                "module": "sqlite-framework",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.lifecycle",
                "module": "lifecycle-service",
                "version": {
                  "requires": "2.1.0"
                },
                "reason": "Required by androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.tracing",
                "module": "tracing",
                "version": {
                  "requires": "1.0.0"
                },
                "reason": "Required by androidx.startup:startup-runtime:1.0.0, androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "com.google.guava",
                "module": "failureaccess",
                "version": {
                  "requires": "1.0.1"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "com.google.code.findbugs",
                "module": "jsr305",
                "version": {
                  "requires": "3.0.2"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "org.checkerframework",
                "module": "checker-qual",
                "version": {
                  "requires": "3.12.0"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "com.google.errorprone",
                "module": "error_prone_annotations",
                "version": {
                  "requires": "2.11.0"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "com.google.j2objc",
                "module": "j2objc-annotations",
                "version": {
                  "requires": "1.3"
                },
                "reason": "Required by androidx.privacysandbox.ads:ads-adservices-java:1.0.0-beta05, androidx.privacysandbox.ads:ads-adservices:1.0.0-beta05, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads:24.9.0"
              },
              {
                "group": "androidx.room",
                "module": "room-common",
                "version": {
                  "requires": "2.2.5"
                },
                "reason": "Required by androidx.work:work-runtime:2.7.0, com.google.ads.mediation:ironsource:9.3.0.1, com.google.android.gms:play-services-ads-api:24.9.0, com.google.android.gms:play-services-ads:24.9.0"
              }
            ]
          }
        ]
      }
    """.trimIndent()
  }
}
