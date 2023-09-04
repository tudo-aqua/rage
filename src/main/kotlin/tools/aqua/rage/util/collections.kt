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
 * A version of [MutableMap.computeIfAbsent] that does not softlock the map and therefore can be
 * used for dynamic programming.
 *
 * @param K the key type
 * @param V the value type
 * @param key the key to read or compute.
 * @param mappingFunction computes the value for key iff key is missing in the map.
 * @return the value associated with key or [mappingFunction] applied to the [key].
 */
fun <K, V> MutableMap<K, V>.computeIfAbsentDynamic(key: K, mappingFunction: (K) -> V): V {
  this[key]?.also {
    return it
  }
  return mappingFunction(key).also { this[key] = it }
}

/** True iff [this] and [other] do not share any elements. */
infix fun <T> Iterable<T>.disjoint(other: Iterable<T>) = (this intersect other.toSet()).isEmpty()

/**
 * Split this list into [buckets] disjoint sublists such that the bucket sizes are as similar as
 * possible.
 *
 * If the list's size is divisible by [buckets] all buckets will be of size `size/`[buckets]. If
 * not, let `k` be the remainder. The first `k` buckets will be of size `1+size/`[buckets]` and the
 * remainder of size `size/`[buckets].
 */
fun <T> List<T>.bucketed(buckets: Int): List<List<T>> {
  val largeBuckets = size % buckets
  val smallSize = (size / buckets)
  val largeSize = smallSize + 1
  return subList(0, largeSize * largeBuckets).chunked(largeSize) +
      (if (smallSize == 0) emptyList()
      else subList(largeSize * largeBuckets, size).chunked(smallSize))
}
