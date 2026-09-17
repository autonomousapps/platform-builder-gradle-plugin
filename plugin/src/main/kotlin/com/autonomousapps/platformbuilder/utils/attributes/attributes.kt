// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.utils.attributes

import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category

/** This is different from [org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE], which has type `Category`. */
private val CATEGORY: Attribute<String> = Attribute.of("org.gradle.category", String::class.java)
private val PLATFORM_CATEGORIES = listOf(Category.REGULAR_PLATFORM, Category.ENFORCED_PLATFORM)

internal fun ResolvedDependencyResult.isJavaPlatform(): Boolean = selected.variants.any { variant ->
  val strongCategory = variant.attributes.getAttribute(Category.CATEGORY_ATTRIBUTE)?.name
  val stringCategory = variant.attributes.getAttribute(CATEGORY)

  stringCategory in PLATFORM_CATEGORIES || strongCategory in PLATFORM_CATEGORIES
}
