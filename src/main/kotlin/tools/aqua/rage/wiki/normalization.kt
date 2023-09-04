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

package tools.aqua.rage.wiki

/**
 * An instance of [Or] in disjunctive normal form, containing [disjuncts] that are guaranteed to be
 * [DNFAnd]s.
 */
data class DNFOr(override val disjuncts: List<DNFAnd>) : Or {
  /** Utility constructor that creates an empty clause list. */
  constructor() : this(emptyList())
  /** Utility constructor that accepts [disjuncts] as varargs. */
  constructor(vararg disjuncts: DNFAnd) : this(disjuncts.toList())
}

/**
 * An instance of [And] in disjunctive normal form, containing [conjuncts] that are guaranteed to be
 * [BinaryRelation]s.
 */
data class DNFAnd(override val conjuncts: List<BinaryRelation>) : And {
  /** Utility constructor that creates an empty clause list. */
  constructor() : this(emptyList())
  /** Utility constructor that accepts [conjuncts] as varargs. */
  constructor(vararg conjuncts: BinaryRelation) : this(conjuncts.toList())
}

/** Convert this guard to disjunctive normal form. */
fun Guard.toDisjunctiveNormalForm(): DNFOr =
    when (this) {
      is True -> DNFOr()
      is And ->
          conjuncts
              .map { it.toDisjunctiveNormalForm() }
              .reduce { left, right ->
                DNFOr(
                    left.disjuncts.flatMap { leftDisjunct ->
                      right.disjuncts.map { rightDisjunct ->
                        DNFAnd(leftDisjunct.conjuncts + rightDisjunct.conjuncts)
                      }
                    })
              }
      is Or ->
          disjuncts
              .map { it.toDisjunctiveNormalForm() }
              .reduce { left, right -> DNFOr(left.disjuncts + right.disjuncts) }
      is BinaryRelation -> DNFOr(DNFAnd(this))
    }

/**
 * Simplify all inequalities in this guard (i.e., greater-or-equal becomes greater logical-or equal
 * etc.).
 */
fun Guard.simplifyInequalities(): Guard =
    when (this) {
      is GreaterEqual -> ComplexOr(Greater(left, right), Equal(left, right))
      is LessEqual -> ComplexOr(Less(left, right), Equal(left, right))
      is And -> ComplexAnd(conjuncts.map { it.simplifyInequalities() })
      is Or -> ComplexOr(disjuncts.map { it.simplifyInequalities() })
      else -> this
    }
