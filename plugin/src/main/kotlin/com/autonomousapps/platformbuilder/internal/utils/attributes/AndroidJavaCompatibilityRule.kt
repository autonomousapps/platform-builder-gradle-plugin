// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.attributes

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.java.TargetJvmEnvironment

/**
 * TODO.
 */
internal class AndroidJavaCompatibilityRule : AttributeCompatibilityRule<TargetJvmEnvironment> {
  override fun execute(details: CompatibilityCheckDetails<TargetJvmEnvironment>) {
    val producerValue = details.producerValue ?: return

    println("prod value = ${producerValue.name}")

    when (producerValue.name) {
      TargetJvmEnvironment.ANDROID, TargetJvmEnvironment.STANDARD_JVM -> details.compatible()
      else -> details.incompatible()
    }
  }
}

// TODO: This one seems more important
internal class AarJarCompatibilityRule : AttributeCompatibilityRule<LibraryElements> {
  override fun execute(details: CompatibilityCheckDetails<LibraryElements>) {
    val consumerValue = details.consumerValue ?: return
    val producerValue = details.producerValue ?: return

    println("cons value = ${consumerValue.name}")
    println("prod value = ${producerValue.name}")

    // if producer is a Java library, that's fine
//    if (consumerValue.name == "aar" && producerValue.name == "jar") {
//      details.compatible()
//    }

    when (producerValue.name) {
      "aar", LibraryElements.JAR -> details.compatible()
      else -> details.incompatible()
    }
  }
}
