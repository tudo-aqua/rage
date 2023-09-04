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

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import tools.aqua.rage.util.nullReplacing
import tools.aqua.rage.util.nullReplacingEmpty
import tools.aqua.rage.util.nullReplacingFalse

/**
 * A register automaton in Automata Wiki XML syntax.
 *
 * @property alphabet the RA's alphabet
 * @property constants the constants (i.e., immutable registers)
 * @property globals the mutable registers
 * @property locations the locations in the RA
 * @property transitions the transition structure.
 */
@Serializable
@XmlSerialName("register-automaton")
data class WikiRegisterAutomaton(
    @XmlSerialName("alphabet") val alphabet: Alphabet,
    @XmlChildrenName("constant") val constants: List<Register>,
    @XmlChildrenName("variable") val globals: List<Register>,
    @XmlChildrenName("location") val locations: List<Location>,
    @XmlChildrenName("transition") val transitions: List<Transition>
)

/** An RA alphabet, consisting of [inputs] and [outputs]. */
@Serializable
data class Alphabet(
    @XmlChildrenName("symbol") val inputs: List<Symbol>,
    @XmlChildrenName("symbol") val outputs: List<Symbol>
)

/** A symbol, containing a [name] and [params]. */
@Serializable
@Suppress("DataClassPrivateConstructor")
data class Symbol
private constructor(
    val name: String,
    @XmlSerialName("param") private val _params: List<Parameter>?
) {
  companion object {
    /** Construct a new [Symbol] for the given [name] and [params]. */
    operator fun invoke(name: String, params: List<Parameter>) =
        Symbol(name, params.ifEmpty { null })
  }

  val params: List<Parameter> by nullReplacingEmpty(::_params)

  override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is Symbol -> false
        else -> name == other.name && params == other.params
      }

  override fun hashCode(): Int = 31 * name.hashCode() + params.hashCode()

  override fun toString(): String = "Symbol(name='$name', params=$params)"
}

/** A data type for registers and parameters. */
@Serializable
enum class Type {
  /** An integer number. */
  @SerialName("int") INT
}

/** A label's parameters, consisting of a [name] and a [type]. */
@Serializable data class Parameter(val name: String, @XmlElement(false) val type: Type)

/** An RA's register, consisting of a [name], a [type], and an initial [value]. */
@Serializable
data class Register(
    val name: String,
    @XmlElement(false) val type: Type,
    @XmlValue val value: String
)

/** A location in an RA. Locations can be [initial] and have a [name]. */
@Serializable
@Suppress("DataClassPrivateConstructor")
data class Location
private constructor(
    @XmlSerialName("initial") private val _initial: Boolean? = false,
    val name: String
) {
  constructor(initial: Boolean, name: String) : this(if (initial) initial else null, name)

  val initial: Boolean by nullReplacingFalse(::_initial)

  override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is Location -> false
        else -> name == other.name && initial == other.initial
      }

  override fun hashCode(): Int = 31 * name.hashCode() + initial.hashCode()

  override fun toString(): String = "Symbol(name='$name', initial=$initial)"
}

/**
 * A transition in an RA.
 *
 * @property from the source location name.
 * @property _params the symbol parameters' relabeling.
 * @property symbol the symbol's name.
 * @property to the target location.
 * @property _guard the transition guard.
 * @property _assignments the assignments made by the transition.
 */
@Serializable
@Suppress("DataClassPrivateConstructor")
data class Transition
private constructor(
    val from: String,
    @Serializable(with = CSVSerializer::class)
    @XmlSerialName("params")
    private val _params: List<String>?,
    val symbol: String,
    val to: String,
    @Contextual
    @XmlSerialName("guard")
    private val _guard: Guard? = null, // context allows selecting the RALib-safe codec
    @XmlSerialName("assignments")
    @XmlChildrenName("assign")
    private val _assignments: List<Assignment>? = null
) {
  companion object {
    /**
     * Create a new transition with the given [from], [params], [symbol], [to], [guard], and
     * [assignments].
     */
    operator fun invoke(
        from: String,
        params: List<String>,
        symbol: String,
        to: String,
        guard: Guard,
        assignments: List<Assignment>
    ) =
        Transition(
            from,
            params.ifEmpty { null },
            symbol,
            to,
            if (guard is True) null else guard,
            assignments.ifEmpty { null })
  }

  /** The symbol parameters' relabeling, with null internally encoding the empty list. */
  val params: List<String> by nullReplacingEmpty(::_params)
  /** The transition guard, with null internally encoding [True]. */
  val guard: Guard by nullReplacing(::_guard) { True }
  /** The assignments made by the transition, with null internally encoding the empty list. */
  val assignments: List<Assignment> by nullReplacingEmpty(::_assignments)

  override fun equals(other: Any?): Boolean =
      when {
        this === other -> true
        other !is Transition -> false
        else ->
            from == other.from &&
                params == other.params &&
                symbol == other.symbol &&
                to == other.to &&
                guard == other.guard &&
                assignments == other.assignments
      }

  override fun hashCode(): Int =
      31 *
          (31 *
              (31 * (31 * (31 * from.hashCode() + params.hashCode()) + symbol.hashCode()) +
                  to.hashCode()) + guard.hashCode()) + assignments.hashCode()

  override fun toString(): String =
      "Transition(from='$from', params=$params, symbol='$symbol', to='$to', guard=$guard, assignments=$assignments)"
}

/** A guard in the Automata Wiki expression syntax. */
sealed interface Guard

/** The empty guard, always accepts. */
data object True : Guard

/** A conjunction with arbitrary [conjuncts]. */
interface And : Guard {
  val conjuncts: List<Guard>
}

/** A conjunction with arbitrary [conjuncts] (implementation class). */
data class ComplexAnd(override val conjuncts: List<Guard>) : And {
  /** Helper constrcutor accepting [conjuncts] as varargs. */
  constructor(vararg conjuncts: Guard) : this(conjuncts.toList())
}

/** A disjunction with arbitrary [disjuncts]. */
interface Or : Guard {
  val disjuncts: List<Guard>
}

/** A disjunction with arbitrary [disjuncts] (implementation class). */
data class ComplexOr(override val disjuncts: List<Guard>) : Or {
  /** Helper constrcutor accepting [disjuncts] as varargs. */
  constructor(vararg disjuncts: Guard) : this(disjuncts.toList())
}

/**
 * A relation in the Automata Wiki expression syntax, consisting of a [left] and [right] symbol and
 * an operation implied by the subclass.
 */
sealed interface BinaryRelation : Guard {
  val left: Expression
  val right: Expression
}

/** Equality between two values. */
data class Equal(override val left: Expression, override val right: Expression) : BinaryRelation

/** Inequality between two values. */
data class NotEqual(override val left: Expression, override val right: Expression) : BinaryRelation

/** The left value is greater than or equal to the right. */
data class GreaterEqual(override val left: Expression, override val right: Expression) :
    BinaryRelation

/** The left value is strictly greater than the right. */
data class Greater(override val left: Expression, override val right: Expression) : BinaryRelation
/** The left value is less than or equal to the right. */
data class LessEqual(override val left: Expression, override val right: Expression) :
    BinaryRelation
/** The left value is strictly less than the right. */
data class Less(override val left: Expression, override val right: Expression) : BinaryRelation

/** A symbol in the Automata Wiki expression syntax. */
sealed interface Expression
/** A variable with an [identifier] in the Automata Wiki expression syntax. */
data class Variable(val identifier: String) : Expression

/** A constant with an [value] in the Automata Wiki expression syntax. */
data class Constant(val value: Int) : Expression

/**
 * An assignment in a transition.
 *
 * @property to the target register.
 * @property from the source value, can be a register, parameter, or constant literal.
 */
@Serializable
data class Assignment(
    val to: String,
    @Serializable(with = ExpressionSerializer::class) @XmlValue val from: Expression
)
