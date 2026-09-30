// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.attributes

import com.android.build.api.attributes.BuildTypeAttr
import org.gradle.api.attributes.AttributeCompatibilityRule
import org.gradle.api.attributes.AttributeDisambiguationRule
import org.gradle.api.attributes.CompatibilityCheckDetails
import org.gradle.api.attributes.MultipleCandidatesDetails

/** We prefer to consume Android libraries targeting the `release` build type. */
internal abstract class AndroidBuildTypeCompatibilityRule : AttributeCompatibilityRule<BuildTypeAttr> {
  override fun execute(details: CompatibilityCheckDetails<BuildTypeAttr>) {
    val producerValue = details.producerValue ?: return

    when (producerValue.name) {
      "release", "debug" -> details.compatible()
      else -> details.incompatible()
    }
  }
}

internal abstract class AndroidBuildTypeDisambiguationRule : AttributeDisambiguationRule<BuildTypeAttr> {
  override fun execute(details: MultipleCandidatesDetails<BuildTypeAttr>): Unit = details.run {
    // may be null
    if (consumerValue != null && consumerValue in candidateValues) {
      details.closestMatch(consumerValue!!)
    }

    val release = candidateValues.find { it.name == "release" }
    if (consumerValue == null && release != null) {
      details.closestMatch(release)
    }
  }
}
