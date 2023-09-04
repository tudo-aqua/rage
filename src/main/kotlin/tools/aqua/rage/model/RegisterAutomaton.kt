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

package tools.aqua.rage.model

// language

/**
 * Symbols are either parameters or registers. [name] should be a human-readable name and is used
 * for binding in a valuation.
 */
sealed interface Symbol {
  val name: String
}

/** A parameter of a given [LabeledSymbol]. */
data class Parameter(override val name: String) : Symbol {
  override fun toString(): String = name
}

/**
 * The input symbols for register automata. These consist of a [label] (usually, a string or
 * similar) and a list of [parameters] that may be empty.
 */
data class LabeledSymbol<out Label>(val label: Label, val parameters: List<Parameter>) {
  init {
    require(parameters.toSet().size == parameters.size) { "duplicate parameters are not allowed" }
  }

  /** Convenience constructor that accepts the [parameters] as a vararg. */
  constructor(label: Label, vararg parameters: Parameter) : this(label, parameters.toList())

  /**
   * Convenience constructor that accepts the [parameters] as a vararg of strings that are used as
   * parameter names.
   */
  constructor(label: Label, vararg parameters: String) : this(label, parameters.map(::Parameter))

  override fun toString(): String = parameters.joinToString(", ", "$label(", ")")
}

/** Get the [index]'th parameter of this symbol. */
operator fun LabeledSymbol<*>.get(index: Int) = parameters[index]

/** Convenience notation for labels that allows declaring labels like `"label"("p0", "p1")`. */
operator fun <Label> Label.invoke(vararg parameters: String): LabeledSymbol<Label> =
    LabeledSymbol(this, parameters.map(::Parameter))

// graph structure

/**
 * A location in a register automaton.
 *
 * These should be implemented as inner classes instead of copying the transition structure
 * information into data classes.
 *
 * @param Label the label alphabet.
 * @param Alphabet the data alphabet.
 * @param GuardTheory the guard language used.
 */
interface Location<out Label, out Alphabet, out GuardTheory> {
  /** The human-readable name. */
  val name: String
  /** True iff the location is initial. */
  val isInitial: Boolean
  /** True iff the location is accepting. */
  val isAccepting: Boolean
  /** The outgoing transitions (including self loops). */
  val outgoing: Set<Transition<Label, Alphabet, GuardTheory>>
  /** The incoming transitions (including self loops). */
  val incoming: Set<Transition<Label, Alphabet, GuardTheory>>
  /** The transitions that are self-loops on this location. */
  val loops: Set<Transition<Label, Alphabet, GuardTheory>>
    get() = outgoing intersect incoming
}

/**
 * A transition in a register automaton.
 *
 * These should be implemented as inner classes instead of copying the transition structure
 * information into data classes.
 *
 * @param Label the label alphabet.
 * @param Alphabet the data alphabet.
 * @param GuardTheory the guard language used.
 */
interface Transition<out Label, out Alphabet, out GuardTheory> {
  /** The transition's source location (i.e., it is outgoing there). */
  val from: Location<Label, Alphabet, GuardTheory>
  /**
   * The symbol recognized by this transition. It matches inputs with the symbol's label and assigns
   * the data values to the parameters.
   */
  val symbol: LabeledSymbol<Label>
  /**
   * The guard for this transition. The transition is enabled if the guard is satisfied under the
   * valuation resulting from the current register values and the bound parameters for a given
   * input.
   */
  val guard: GuardTheory
  /**
   * The transition's assignments. Each register in the map's keys is assigned the value of the
   * symbol (register or parameter).
   */
  val assignment: Map<Register, Symbol>
  /** The transition's target location (i.e., it is incoming there). */
  val to: Location<Label, Alphabet, GuardTheory>
}

// registers

/** A register of an RA. */
data class Register(override val name: String) : Symbol {
  override fun toString(): String = name
}

// guards

/**
 * An abstract representation for formulae, e.g. in a guard.
 *
 * A theory should provide a sealed interface inheriting from this interface that is implemented by
 * all predicates of the theory.
 *
 * @param Model the underlying universe.
 */
interface Formula<Model> {
  /** The free variables in the formula that can be bound to registers and parameters. */
  val freeVariables: Set<Symbol>

  /**
   * Returns `true` iff the formula is satisfied when binding the free variables to the values
   * provided in the [valuation].
   */
  fun isSatisfiedUnder(valuation: Map<Symbol, Model>): Boolean
}

/**
 * A read-only register automaton. RAs always have at least an initial location and can have
 * registers with optional initialization, locations and transitions.
 *
 * @param Label the label alphabet.
 * @param Alphabet the data alphabet.
 * @param GuardTheory the guard language used.
 */
interface RegisterAutomaton<out Label, Alphabet, out GuardTheory : Formula<Alphabet>> {
  /** The RA's initial location. */
  val initialLocation: Location<Label, Alphabet, GuardTheory>
    get() = locations.single { it.isAccepting }

  /** All the RA's locations. */
  val locations: Set<Location<Label, Alphabet, GuardTheory>>
  /** All the RA's accepting locations. */
  val acceptingLocations: Set<Location<Label, Alphabet, GuardTheory>>
    get() = locations.filterTo(mutableSetOf()) { it.isAccepting }

  /** The RA's registers. */
  val registers: Set<Register>
  /**
   * The initial valuation for all registers that have one. Non-initialized registers are omitted.
   */
  val initialValuation: Map<Register, Alphabet>
  /** The RA's transitions as an unstructured set. */
  val transitions: Set<Transition<Label, Alphabet, GuardTheory>>
}

/**
 * A register automaton that can be extended with new registers etc., but that does not support
 * removal.
 *
 * @param Label the label alphabet.
 * @param Alphabet the data alphabet.
 * @param GuardTheory the guard language used.
 */
interface BuildableRegisterAutomaton<Label, Alphabet, GuardTheory : Formula<Alphabet>> :
    RegisterAutomaton<Label, Alphabet, GuardTheory> {
  /** Add a new location with name [name] and acceptance [isAccepting]. */
  fun addLocation(name: String, isAccepting: Boolean): Location<Label, Alphabet, GuardTheory>
  /** Add a new non-initialized register with name [name]. */
  fun addRegister(name: String): Register
  /** Add a new initialized register with name [name] and initial value [initialValuation]. */
  fun addRegister(name: String, initialValuation: Alphabet): Register

  /**
   * Add a new transition from [from] to [to], with label [symbol], guard [guard] and assignment
   * [assignment] (expresses as target to source).
   */
  fun addTransition(
      from: Location<Label, Alphabet, GuardTheory>,
      symbol: LabeledSymbol<Label>,
      guard: GuardTheory,
      assignment: Map<Register, Symbol>,
      to: Location<Label, Alphabet, GuardTheory>
  ): Transition<Label, Alphabet, GuardTheory>
}

/**
 * An implementation of an RA that stores information centrally and uses dependent inner classes for
 * location and transition objects.
 *
 * @param Label the label alphabet.
 * @param Alphabet the data alphabet.
 * @param GuardTheory the guard language used.
 * @param initialLocationName the initial location's name.
 * @param initialLocationIsAccepting `true` iff the initial location is accepting.
 */
class DefaultRegisterAutomaton<Label, Alphabet, GuardTheory : Formula<Alphabet>>(
    initialLocationName: String,
    initialLocationIsAccepting: Boolean
) : BuildableRegisterAutomaton<Label, Alphabet, GuardTheory> {

  private inner class DefaultLocation(
      override val name: String,
      override val isInitial: Boolean,
      override val isAccepting: Boolean
  ) : Location<Label, Alphabet, GuardTheory> {
    override val outgoing: Set<Transition<Label, Alphabet, GuardTheory>>
      get() = locationNameToOutgoing.getValue(name)

    override val incoming: Set<Transition<Label, Alphabet, GuardTheory>>
      get() = locationNameToIncoming.getValue(name)

    override fun toString(): String = name
  }

  private inner class DefaultTransition(
      val fromName: String,
      override val symbol: LabeledSymbol<Label>,
      override val guard: GuardTheory,
      override val assignment: Map<Register, Symbol>,
      val toName: String,
  ) : Transition<Label, Alphabet, GuardTheory> {
    override val from: Location<Label, Alphabet, GuardTheory>
      get() = locations.single { it.name == fromName }

    override val to: Location<Label, Alphabet, GuardTheory>
      get() = locations.single { it.name == toName }

    override fun toString(): String =
        "<from=$fromName, symbol=$symbol, guard=($guard), assignment=${
              assignment.entries.joinToString(", ", "{", "}") { (target, source) -> "$target ≔ $source" }
          }, to=$toName>"
  }

  private val initialLocationObject =
      DefaultLocation(initialLocationName, true, initialLocationIsAccepting)
  override val initialLocation: Location<Label, Alphabet, GuardTheory> = initialLocationObject
  private val locationNameToObject = mutableMapOf(initialLocationName to initialLocationObject)

  private val registerNameToObject = mutableMapOf<String, Register>()
  private val _initialValuation = mutableMapOf<Register, Alphabet>()

  override val initialValuation: Map<Register, Alphabet>
    get() = _initialValuation

  private val locationNameToOutgoing =
      mutableMapOf(initialLocationName to mutableSetOf<DefaultTransition>())
  private val locationNameToIncoming =
      mutableMapOf(initialLocationName to mutableSetOf<DefaultTransition>())

  override val locations: Set<Location<Label, Alphabet, GuardTheory>>
    get() = locationNameToObject.values.toSet()

  override val registers: Set<Register>
    get() = registerNameToObject.values.toSet()

  override val transitions: Set<Transition<Label, Alphabet, GuardTheory>>
    get() = locationNameToOutgoing.values.flatten().toSet()

  override fun addLocation(
      name: String,
      isAccepting: Boolean
  ): Location<Label, Alphabet, GuardTheory> {
    locationNameToObject[name]?.also {
      require(it.isAccepting == isAccepting) {
        "location $name already exists, but has mismatching acceptance"
      }
      return it
    }
    return DefaultLocation(name, false, isAccepting).also {
      locationNameToObject[name] = it
      locationNameToOutgoing[name] = mutableSetOf()
      locationNameToIncoming[name] = mutableSetOf()
    }
  }

  override fun addRegister(name: String): Register {
    registerNameToObject[name]?.also {
      require(it !in _initialValuation) {
        "register $name already exists, but has initial valuation"
      }
      return it
    }
    return Register(name).also { registerNameToObject[name] = it }
  }

  override fun addRegister(name: String, initialValuation: Alphabet): Register {
    registerNameToObject[name]?.also {
      require(_initialValuation[it] == initialValuation) {
        "register $name already exists, but has mismatching initial valuation"
      }
      return it
    }
    return Register(name).also {
      registerNameToObject[name] = it
      _initialValuation[it] = initialValuation
    }
  }

  override fun addTransition(
      from: Location<Label, Alphabet, GuardTheory>,
      symbol: LabeledSymbol<Label>,
      guard: GuardTheory,
      assignment: Map<Register, Symbol>,
      to: Location<Label, Alphabet, GuardTheory>
  ): Transition<Label, Alphabet, GuardTheory> =
      DefaultTransition(from.name, symbol, guard, assignment, to.name).also {
        locationNameToOutgoing.getValue(from.name) += it
        locationNameToIncoming.getValue(to.name) += it
      }

  override fun toString(): String =
      "DefaultRegisterAutomaton(initialLocation=$initialLocation, locations=$locations, initialValuation=$initialValuation, registers=$registers, transitions=$transitions)"
}
