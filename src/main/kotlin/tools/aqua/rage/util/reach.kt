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
 * Compute the objects in a lattice implied by the function [step] starting with [initial].
 *
 * For example, the reachability in a graph can be identified by defining [step] as the neighbors of
 * a node. Note that if no fixed point is reached, the function does not terminate regularly.
 */
fun <T> reach(initial: T, step: (T) -> Collection<T>): Set<T> =
    reachWithDistance(initial, step).keys

/**
 * Compute the objects in a lattice implied by the function [step] starting with [initial] and
 * return their closest distance to the initial value.
 *
 * For example, the reachability in a graph can be identified by defining [step] as the neighbors of
 * a node. Note that if no fixed point is reached, the function does not terminate regularly.
 */
fun <T> reachWithDistance(initial: T, step: (T) -> Collection<T>): Map<T, Int> {
  var front = mutableSetOf(initial)
  var distanceOfFront = 0
  val result = mutableMapOf(initial to 0)
  do {
    distanceOfFront++
    front = front.flatMapTo(mutableSetOf()) { step(it) }
    val changed = front.map { result.putIfAbsent(it, distanceOfFront) == null }.any { it }
  } while (changed)
  return result
}
