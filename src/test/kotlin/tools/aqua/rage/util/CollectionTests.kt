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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(PER_CLASS)
class CollectionTests {

  @ParameterizedTest
  @MethodSource("bucketCasesTestSource")
  fun `bucketing is correct`(data: List<Int>, nBuckets: Int, expected: List<List<Int>>) {
    assertThat(data.bucketed(nBuckets)).isEqualTo(expected)
  }

  private fun bucketCasesTestSource(): Iterable<Arguments> =
      listOf(
          Arguments.of(listOf(1, 2, 3), 1, listOf(listOf(1, 2, 3))),
          Arguments.of(listOf(1, 2, 3), 2, listOf(listOf(1, 2), listOf(3))),
          Arguments.of(listOf(1, 2, 3), 3, listOf(listOf(1), listOf(2), listOf(3))),
          Arguments.of(listOf(1, 2, 3, 4), 1, listOf(listOf(1, 2, 3, 4))),
          Arguments.of(listOf(1, 2, 3, 4), 2, listOf(listOf(1, 2), listOf(3, 4))),
          Arguments.of(listOf(1, 2, 3, 4), 3, listOf(listOf(1, 2), listOf(3), listOf(4))),
          Arguments.of(listOf(1, 2, 3, 4), 4, listOf(listOf(1), listOf(2), listOf(3), listOf(4))),
          Arguments.of(listOf(1, 2, 3, 4, 5), 1, listOf(listOf(1, 2, 3, 4, 5))),
          Arguments.of(listOf(1, 2, 3, 4, 5), 2, listOf(listOf(1, 2, 3), listOf(4, 5))),
          Arguments.of(listOf(1, 2, 3, 4, 5), 3, listOf(listOf(1, 2), listOf(3, 4), listOf(5))),
          Arguments.of(
              listOf(1, 2, 3, 4, 5), 4, listOf(listOf(1, 2), listOf(3), listOf(4), listOf(5))),
          Arguments.of(
              listOf(1, 2, 3, 4, 5),
              5,
              listOf(listOf(1), listOf(2), listOf(3), listOf(4), listOf(5))),
      )
}
