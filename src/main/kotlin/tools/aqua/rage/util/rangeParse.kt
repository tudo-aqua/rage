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
 * Parse a string into an [IntRange], roughly following the syntactic rules for Kotlin ranges.
 *
 * Multiple options are supported:
 * - `a` returns the inclusive range from `a` to `a`.
 * - `a..b` returns the inclusive range from `a` to `b`.
 * - `a..<b` returns the inclusive range from `a` to `b-1`.
 * - `a..b step k` returns the inclusive range from `a` to `b` with a step size of `k`.
 * - `a..<b step k` returns the inclusive range from `a` to `b-1` with a step size of `k`.
 *
 * In general, whitespace is ignored. The method attempts to throw sensible exceptions on syntactic
 * errors.
 */
fun String.toIntRange(): IntProgression {
  val splitByStep = split("step", ignoreCase = true, limit = 2)

  val rangeStr = splitByStep.first()
  val beginToEnd = rangeStr.split("..", limit = 2)

  if (beginToEnd.size == 1) {
    val valueStr = beginToEnd.single().trim()
    val value = valueStr.toIntOrNull() ?: error("Value \"$valueStr\" is not a valid integer")
    return value..value
  }

  val beginStr = beginToEnd[0].trim()
  val begin = beginStr.toIntOrNull() ?: error("Range start \"$beginStr\" is not a valid integer")

  val endStr = beginToEnd[1].trim()
  val range =
      if (endStr.firstOrNull() == '<') {
        val end =
            endStr.substring(1).trim().toIntOrNull()
                ?: error("Range end \"$endStr\" is not a valid integer")
        begin ..< end
      } else {
        val end = endStr.toIntOrNull() ?: error("Range end \"$endStr\" is not a valid integer")
        begin..end
      }

  if (splitByStep.size == 2) {
    val stepStr = splitByStep[1].trim()
    val step = stepStr.toIntOrNull() ?: error("Step \"$stepStr\" is not a valid integer")

    return range step step
  }

  return range
}
