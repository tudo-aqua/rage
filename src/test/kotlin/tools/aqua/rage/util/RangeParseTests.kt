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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
class RangeParseTests {

  @ParameterizedTest
  @MethodSource("rangesTestSource")
  fun `range parses correctly`(input: String, expected: IntProgression) {
    val parsed = assertDoesNotThrow { input.toIntRange() }
    assertThat(parsed).isEqualTo(expected)
  }

  private fun rangesTestSource(): Iterable<Arguments> =
      listOf(
              listOf("42") to 42..42,
              listOf("23", "..", "42") to 23..42,
              listOf("23", "..", "<", "42") to 23 ..< 42,
              listOf("23", "..", "42", "step", "5") to (23..42 step 5),
              listOf("23", "..", "<", "42", "step", "5") to (23 ..< 42 step 5))
          .flatMap { (tokens, result) ->
            generateSpacedVariants(listOf("") + tokens).map { it to result }
          }
          .map { (string, result) -> Arguments.of(string, result) }

  private fun generateSpacedVariants(tokens: List<String>): List<String> =
      if (tokens.isEmpty()) listOf("")
      else {
        val first = tokens.first()
        val tails = generateSpacedVariants(tokens.subList(1, tokens.size))
        tails.map { "$first$it" } + tails.map { "$first $it" }
      }
}
