// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.attributes

import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.LibraryElements

/** We don't care if we're consuming AARs or JARs. */
internal class AarJarCompatibilityRule : AttributeCompatibilityRule<LibraryElements> {
  override fun execute(details: CompatibilityCheckDetails<LibraryElements>) {
    val producerValue = details.producerValue ?: return

    when (producerValue.name) {
      "aar", LibraryElements.JAR -> details.compatible()
      else -> details.incompatible()
    }
  }
}
