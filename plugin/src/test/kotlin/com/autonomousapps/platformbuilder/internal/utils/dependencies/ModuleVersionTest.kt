// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.internal.utils.dependencies

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class ModuleVersionTest {

  @Test
  fun `plain requiredVersion is not rich version`() {
    assertThat(ModuleVersion(requiredVersion = "1.0").isRichVersionRequest()).isFalse()
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(
    strings = [
      "[1, 2]",
      "(1, 2)",
      "1+",
      "latest.release",
      "latest.integration"
    ]
  )
  fun `requiredVersion with rich signals is a rich version`(version: String) {
    assertThat(ModuleVersion(requiredVersion = version).isRichVersionRequest()).isTrue()
  }

  @Test
  fun `preferredVersion is a rich version`() {
    assertThat(ModuleVersion(requiredVersion = "1.0", preferredVersion = "1.1").isRichVersionRequest()).isTrue()
  }

  @Test
  fun `strictVersion is a rich version`() {
    assertThat(ModuleVersion(requiredVersion = "1.0", strictVersion = "1.1").isRichVersionRequest()).isTrue()
  }
}