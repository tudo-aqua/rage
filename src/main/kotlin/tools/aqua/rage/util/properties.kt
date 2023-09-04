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

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Wrap a backing accessor [wrapped] that uses `null` equivalently to an empty list into a null-safe
 * property.
 */
fun <T, W> nullReplacingEmpty(wrapped: () -> List<W>?): ReadOnlyProperty<T, List<W>> =
    NullReplacingProperty(wrapped) { emptyList() }

/**
 * Wrap a backing accessor [wrapped] that uses `null` equivalently to `false` into a null-safe
 * property.
 */
fun <T> nullReplacingFalse(wrapped: () -> Boolean?): ReadOnlyProperty<T, Boolean> =
    NullReplacingProperty(wrapped) { false }

/**
 * Wrap a backing accessor [wrapped] that uses `null` equivalently to the result of
 * [nullReplacement] into a null-safe property.
 */
fun <T, V : Any> nullReplacing(
    wrapped: () -> V?,
    nullReplacement: () -> V
): ReadOnlyProperty<T, V> = NullReplacingProperty(wrapped, nullReplacement)

/**
 * A property that wraps a backing accessor [wrapped] that uses `null` equivalently to the result of
 * [nullReplacement] into a null-safe property.
 */
class NullReplacingProperty<T, V : Any>(
    private val wrapped: () -> V?,
    private val nullReplacement: () -> V
) : ReadOnlyProperty<T, V> {
  override fun getValue(thisRef: T, property: KProperty<*>): V = wrapped() ?: nullReplacement()
}
