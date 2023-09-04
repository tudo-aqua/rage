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
 * Perform a generalized zip operation on two sequences.
 *
 * While both sequences have remaining elements, take the first of this and [other], apply
 * [combiner] and yield the result.
 */
fun <T, U, V> Sequence<T>.combine(other: Sequence<U>, combiner: (T, U) -> V) = sequence {
  val left = iterator()
  val right = other.iterator()

  while (left.hasNext() && right.hasNext()) {
    yield(combiner(left.next(), right.next()))
  }
}

/** Convert this into an infinite sequence of itself. */
fun <T> T.toInfiniteSequence(): Sequence<T> = sequence {
  while (true) {
    yield(this@toInfiniteSequence)
  }
}
