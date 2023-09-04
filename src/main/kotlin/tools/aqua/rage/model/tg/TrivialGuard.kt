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

package tools.aqua.rage.model.tg

import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.Symbol

/** A guard implementation that only supports the true guard, mostly used in testing. */
sealed interface TrivialGuard<T> : Formula<T>

/** The true guard. */
class True<T> : TrivialGuard<T> {
  override val freeVariables: Set<Symbol> = emptySet()

  override fun isSatisfiedUnder(valuation: Map<Symbol, T>): Boolean = true

  override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is True<*> -> false
        else -> true
      }

  override fun hashCode(): Int = 0

  override fun toString(): String = "⊤"
}
