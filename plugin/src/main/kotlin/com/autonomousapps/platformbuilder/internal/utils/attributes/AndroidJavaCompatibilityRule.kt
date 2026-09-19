// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.attributes

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.java.TargetJvmEnvironment

/** We don't care whether we're consuming libraries targeted for a Java or Android environment. */
internal class AndroidJavaCompatibilityRule : AttributeCompatibilityRule<TargetJvmEnvironment> {
  override fun execute(details: CompatibilityCheckDetails<TargetJvmEnvironment>) {
    val producerValue = details.producerValue ?: return

    when (producerValue.name) {
      TargetJvmEnvironment.ANDROID, TargetJvmEnvironment.STANDARD_JVM -> details.compatible()
      else -> details.incompatible()
    }
  }
}
