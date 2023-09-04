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

import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.Symbol

/**
 * Root class for guards on comparable objects with equality, inequality, and ordering.
 *
 * @param T the comparable object type.
 */
sealed interface InequalityGuard<T : Comparable<T>> : Formula<T>

/**
 * The guard that always accepts.
 *
 * @param T the comparable object type.
 */
class True<T : Comparable<T>> : InequalityGuard<T> {
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

/**
 * Root class for guards with variadic arity wrapping other guard objects.
 *
 * @param T the comparable object type.
 */
sealed class VariadicGuard<T : Comparable<T>> : InequalityGuard<T> {
  /** The guards contained in the guard. */
  protected abstract val elements: List<InequalityGuard<T>>

  override val freeVariables: Set<Symbol>
    get() = elements.flatMapTo(mutableSetOf()) { it.freeVariables }
}

/**
 * The logical and operator on guards.
 *
 * @param T the comparable object type.
 * @property conjuncts the guards being conjugated.
 */
data class And<T : Comparable<T>>(val conjuncts: List<InequalityGuard<T>>) : VariadicGuard<T>() {
  /** Utility constructor accepting [conjuncts] as varargs. */
  constructor(vararg conjuncts: InequalityGuard<T>) : this(conjuncts.toList())

  override val elements: List<InequalityGuard<T>> = conjuncts

  override fun isSatisfiedUnder(valuation: Map<Symbol, T>): Boolean =
      conjuncts.all { it.isSatisfiedUnder(valuation) }

  override fun toString(): String = conjuncts.joinToString(" ∧ ") { "($it)" }
}

/**
 * The logical or operator on guards.
 *
 * @param T the comparable object type.
 * @property disjuncts the guards being disjugated.
 */
data class Or<T : Comparable<T>>(val disjuncts: List<InequalityGuard<T>>) : VariadicGuard<T>() {
  /** Utility constructor accepting [disjuncts] as varargs. */
  constructor(vararg disjuncts: InequalityGuard<T>) : this(disjuncts.toList())

  override val elements: List<InequalityGuard<T>> = disjuncts

  override fun isSatisfiedUnder(valuation: Map<Symbol, T>): Boolean =
      disjuncts.any { it.isSatisfiedUnder(valuation) }

  override fun toString(): String = disjuncts.joinToString(" ∧ ") { "($it)" }
}

/** Root class for guards with binary arity combining elements of [T]. */
sealed class BinaryGuard<T : Comparable<T>> : InequalityGuard<T> {
  /** The left operand. */
  abstract val left: Symbol
  /** The right operand. */
  abstract val right: Symbol

  override val freeVariables: Set<Symbol>
    get() = setOf(left, right)

  override fun isSatisfiedUnder(valuation: Map<Symbol, T>): Boolean =
      isSatisfiedBy(valuation.getValue(left), valuation.getValue(right))

  /**
   * Compact implementation of [isSatisfiedUnder]; must return true iff [left] `○` [right] for the
   * implemented operation `○`.
   */
  abstract fun isSatisfiedBy(left: T, right: T): Boolean
}

/** The equality operator on [T] (i.e., [left] = [right]). */
data class Equals<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left == right

  override fun toString(): String = "$left = $right"
}

/** The inequality operator on [T] (i.e., [left] ≠ [right]). */
data class NotEquals<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left != right

  override fun toString(): String = "$left ≠ $right"
}

/** The greater-or-equal operator on [T] (i.e., [left] ≥ [right]). */
data class GreaterEquals<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left >= right

  override fun toString(): String = "$left ≥ $right"
}
/** The greater operator on [T] (i.e., [left] > [right]). */
data class Greater<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left > right

  override fun toString(): String = "$left > $right"
}
/** The less-or-equal operator on [T] (i.e., [left] ≤ [right]). */
data class LessEquals<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left <= right

  override fun toString(): String = "$left ≤ $right"
}

/** The less operator on [T] (i.e., [left] < [right]). */
data class Less<T : Comparable<T>>(override val left: Symbol, override val right: Symbol) :
    BinaryGuard<T>() {
  override fun isSatisfiedBy(left: T, right: T): Boolean = left < right

  override fun toString(): String = "$left < $right"
}

/** Exception thrown when trying to call [invert] on a guard containing an instance of [True]. */
class TrueGuardException : RuntimeException()

/** Perform negation on the given guard object by applying DeMorgan's laws. */
fun <T : Comparable<T>> InequalityGuard<T>.invert(): InequalityGuard<T> =
    when (this) {
      is True -> throw TrueGuardException()
      is And -> Or(conjuncts.map(InequalityGuard<T>::invert))
      is Or -> And(disjuncts.map(InequalityGuard<T>::invert))
      is Equals -> NotEquals(left, right)
      is NotEquals -> Equals(left, right)
      is GreaterEquals -> Less(left, right)
      is Greater -> LessEquals(left, right)
      is LessEquals -> Greater(left, right)
      is Less -> GreaterEquals(left, right)
    }
