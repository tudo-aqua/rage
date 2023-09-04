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

/** Transform this guard into the Automata Wiki expression language. */
fun Guard.toAutomataWikiString(): String =
    when (this) {
      True -> ""
      is And -> conjuncts.joinToString(" && ", "(", ")") { it.toAutomataWikiString() }
      is Or -> disjuncts.joinToString(" || ", "(", ")") { it.toAutomataWikiString() }
      is Equal -> "(${left.toAutomataWikiString()} == ${right.toAutomataWikiString()})"
      is NotEqual -> "(${left.toAutomataWikiString()} != ${right.toAutomataWikiString()})"
      is GreaterEqual -> "(${left.toAutomataWikiString()} >= ${right.toAutomataWikiString()})"
      is Greater -> "(${left.toAutomataWikiString()} > ${right.toAutomataWikiString()})"
      is LessEqual -> "(${left.toAutomataWikiString()} <= ${right.toAutomataWikiString()})"
      is Less -> "(${left.toAutomataWikiString()} < ${right.toAutomataWikiString()})"
    }

/**
 * Transform this guard into the RALib-compatible subset of the Automata Wiki expression language.
 */
fun DNFOr.toRALibSafeAutomataWikiString(): String =
    disjuncts.joinToString("||") { it.toRALibSafeAutomataWikiString() }

/**
 * Transform this guard into the RALib-compatible subset of the Automata Wiki expression language.
 */
fun DNFAnd.toRALibSafeAutomataWikiString(): String =
    conjuncts.joinToString("&&") { it.toRALibSafeAutomataWikiString() }

/**
 * Transform this guard into the RALib-compatible subset of the Automata Wiki expression language ot
 * throw an error if the guard was not transformed with [simplifyInequalities].
 */
fun BinaryRelation.toRALibSafeAutomataWikiString(): String =
    when (this) {
      is Equal -> "${left.toAutomataWikiString()}==${right.toAutomataWikiString()}"
      is NotEqual -> "${left.toAutomataWikiString()}!=${right.toAutomataWikiString()}"
      is GreaterEqual -> error("operator >= unsupported in RALib")
      is Greater -> "${left.toAutomataWikiString()}>${right.toAutomataWikiString()}"
      is LessEqual -> error("operator <= unsupported in RALib")
      is Less -> "${left.toAutomataWikiString()}<${right.toAutomataWikiString()}"
    }

/** Transform this expression into the Automata Wiki expression language. */
fun Expression.toAutomataWikiString(): String =
    when (this) {
      is Variable -> identifier
      is Constant -> value.toString()
    }
