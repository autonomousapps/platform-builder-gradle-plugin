# Platform Builder Gradle Plugin

A plugin that builds a [Java platform](https://docs.gradle.org/current/userguide/java_platform_plugin.html) (also known
in Maven as a BOM, or Bill of Materials) based on a set of user-declared root dependencies.

The resulting platform contains entries for all root dependencies and their transitive dependencies. Conflict resolution
occurs when building the platform, so any version conflicts encountered will be resolved, with the results included in
the platform.

## Why?

In large projects, failure to use a platform (or some other method to constrain external dependency versions) can result 
in your library modules running their tests against a different runtime classpath than your application expects. When
that happens, your tests can fail (or pass) even though your application runs fine (or crashes) for end-users.

Another reason to have a centralized platform is that it can improve build performance. Without one, an IDE sync will
resolve arbitrarily many different versions of the same dependency, wasting time both in downloading and indexing, as 
well as increasing resource usage in the IDE, harming developer productivity.

## Use in your build

*platform/build.gradle.kts*
```kotlin
plugins {
  id("com.autonomousapps.platform-builder") version "<<version>>"
}

// Optional
platformBuilder {
  enablePublishing()
}

dependencies {
  // Supports Android platforms and libraries
  platformApi(platform("androidx.compose:compose-bom:2026.08.00"))
  
  // Supports Kotlin Multiplatform platforms and libraries
  platformApi(platform("com.squareup.okhttp3:okhttp-bom:5.5.0"))
  
  // Supports Java libraries
  platformApi("org.apache.commons:commons-collections4:4.6.0")
  
  // Supports local projects
  platformApi(project(":provides-constraints"))
  
  // Supports runtime constraints
  platformRuntime(/* as above */)
}
```

*consumer/build.gradle.kts*
```kotlin
dependencies {
  // (1) Use platform-builder project as a platform
  implementation(platform(project(":platform-builder")))
  
  // (2) Omit version. Gradle will use the constraints from the platform
  implementation("com.squareup.okhttp3:okhttp")
  implementation("org.apache.commons:commons-collections4")
}
```

### Publishing

Can be published as a platform via `platformBuilder.enablePublishing()`, or manually as described in the java-platform
[docs](https://docs.gradle.org/current/userguide/java_platform_plugin.html#sec:java_platform_publishing).

## Credits

Inspired by this (at time of writing unmerged) 
[PR](https://github.com/gradle/gradle/pull/38879/changes#diff-d52b99eb1ba6f8cde0dfabf4aac52674a52764fbf087187f11a2cdddc28f88b3) 
to Gradle core.

License
--------

    Copyright 2026 Tony Robalik.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
