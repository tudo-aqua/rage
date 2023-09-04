/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2023-2023 The RAGe Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.aqua.rage.util

/**
 * Arbitrary base-k encoding for numbers. [glyphs] is the alphabet in with this number is encoded.
 *
 * For example, if [glyphs] is `["A", … "Z"]`, the result is base-26 encoding over capital letters,
 * Excel column style.
 */
fun Int.toString(glyphs: List<String>): String {
  if (this < 0) return "-" + (-this).toString(glyphs)

  val radix = glyphs.size
  val remainder = this / radix
  return if (remainder > 0) {
    (remainder - 1).toString(glyphs) + glyphs[this % radix]
  } else {
    glyphs[this % radix]
  }
}
