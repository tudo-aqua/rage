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

import java.math.BigInteger
import kotlin.random.Random
import kotlin.random.asJavaRandom

/**
 * Create a random [BigInteger] between [from] and [until].
 *
 * Since only a bit set of correct size can be randomized this may take multiple iteration for large
 * values of [from].
 */
fun Random.nextBigInteger(from: BigInteger, until: BigInteger): BigInteger {
  require(from <= until)
  if (from == until) return from

  var result: BigInteger
  do {
    result = BigInteger(until.bitLength(), asJavaRandom())
  } while (result < from || result > until)
  return result
}
