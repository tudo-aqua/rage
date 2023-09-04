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

package tools.aqua.rage.model.ig

import tools.aqua.rage.model.Symbol

/** Marker interface for the Guard DSL. */
@DslMarker annotation class GuardDsl

/**
 * Infix operator for logical and on the universe [T]. Combines this guard with [right],
 * intelligently merging [And]s.
 */
@GuardDsl
infix fun <T : Comparable<T>> InequalityGuard<T>.and(right: InequalityGuard<T>): And<T> =
    when {
      this is And<T> && right is And<T> -> And(this.conjuncts + right.conjuncts)
      this is And<T> -> And(this.conjuncts + right)
      right is And<T> -> And(listOf(this) + right.conjuncts)
      else -> And(this, right)
    }

/**
 * Infix operator for logical or on the universe [T]. Combines this guard with [right],
 * intelligently merging [Or]s.
 */
@GuardDsl
infix fun <T : Comparable<T>> InequalityGuard<T>.or(right: InequalityGuard<T>): Or<T> =
    when {
      this is Or<T> && right is Or<T> -> Or(this.disjuncts + right.disjuncts)
      this is Or<T> -> Or(this.disjuncts + right)
      right is Or<T> -> Or(listOf(this) + right.disjuncts)
      else -> Or(this, right)
    }

/** Infix operator for equality on the universe [T]. Compares this symbol with [right]. */
@GuardDsl infix fun <T : Comparable<T>> Symbol.eq(right: Symbol): Equals<T> = Equals(this, right)

/** Infix operator for inequality on the universe [T]. Compares this symbol with [right]. */
@GuardDsl
infix fun <T : Comparable<T>> Symbol.neq(right: Symbol): NotEquals<T> = NotEquals(this, right)

/** Infix operator for greater-or-equal on the universe [T]. Compares this symbol with [right]. */
@GuardDsl
infix fun <T : Comparable<T>> Symbol.geq(right: Symbol): GreaterEquals<T> =
    GreaterEquals(this, right)

/** Infix operator for greater-than on the universe [T]. Compares this symbol with [right]. */
@GuardDsl infix fun <T : Comparable<T>> Symbol.gt(right: Symbol): Greater<T> = Greater(this, right)
/** Infix operator for less-or-equal on the universe [T]. Compares this symbol with [right]. */
@GuardDsl
infix fun <T : Comparable<T>> Symbol.leq(right: Symbol): LessEquals<T> = LessEquals(this, right)
/** Infix operator for less-than on the universe [T]. Compares this symbol with [right]. */
@GuardDsl infix fun <T : Comparable<T>> Symbol.lt(right: Symbol): Less<T> = Less(this, right)
