// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.platformbuilder.utils.files

import org.gradle.api.file.RegularFileProperty
import java.io.File

internal fun RegularFileProperty.getAndDelete(): File {
  val f = get().asFile
  f.delete()
  return f
}
