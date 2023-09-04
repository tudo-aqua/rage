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

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/** Marker interface for the RA DSL. */
@DslMarker annotation class RADsl

/**
 * Create a new register automaton with a fixed initial location.
 *
 * @param Label the register automaton's label alphabet.
 * @param Alphabet the register automaton's data alphabet.
 * @param GuardTheory the register automaton's guard language.
 * @param initialLocationName the name for the initial location.
 * @param initialLocationIsAccepting `true` iff the initial location shall be accepting.
 * @param body the DSL block that configures the register automaton.
 */
@RADsl
fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> ra(
    initialLocationName: String = "initial",
    initialLocationIsAccepting: Boolean = false,
    body: RABuilder<Label, Alphabet, GuardTheory>.() -> Unit
): RegisterAutomaton<Label, Alphabet, GuardTheory> =
    RABuilder<Label, Alphabet, GuardTheory>(initialLocationName, initialLocationIsAccepting)
        .apply { body() }
        .build()

/**
 * The RA builder context.
 *
 * @param Label the register automaton's label alphabet.
 * @param Alphabet the register automaton's data alphabet.
 * @param GuardTheory the register automaton's guard language.
 * @param initialLocationName the name for the initial location.
 * @param initialLocationIsAccepting `true` iff the initial location shall be accepting.
 */
@RADsl
class RABuilder<Label, Alphabet, GuardTheory : Formula<Alphabet>>(
    initialLocationName: String,
    initialLocationIsAccepting: Boolean
) {
  /**
   * Utility for compact `by` additions. Linking a read-only property with this executes the [adder]
   * and returns its result.
   *
   * @param T The type of the property.
   * @property adder the operation hidden by the property.
   */
  inner class GenericDelegatedProvider<T>(
      private inline val adder: DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>.(String) -> T
  ) : PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, T> =
        ra.adder(property.name).let { ReadOnlyProperty { _, _ -> it } }
  }

  private var ra =
      DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>(
          initialLocationName, initialLocationIsAccepting)

  /** The initial location created by the builder. */
  @RADsl val initial: Location<Label, Alphabet, GuardTheory> = ra.initialLocation

  /** Property that creates a new parameter via delegation. */
  @RADsl
  fun parameter(): GenericDelegatedProvider<Parameter> = GenericDelegatedProvider { Parameter(it) }

  /** Create a new parameter named [name]. */
  @RADsl fun parameter(name: String): Parameter = Parameter(name)

  /**
   * Property that adds a new symbol with the given [parameters]; the label is provided via
   * delegation.
   */
  @RADsl
  fun symbol(vararg parameters: Parameter): GenericDelegatedProvider<LabeledSymbol<String>> =
      GenericDelegatedProvider {
        LabeledSymbol(it, parameters.toList())
      }

  /** Create a new symbol with the given label [name] and [parameters]. */
  @RADsl
  fun symbol(name: String, vararg parameters: Parameter): LabeledSymbol<String> =
      LabeledSymbol(name, parameters.toList())

  /**
   * Property that adds a new symbol with the given parameter names [parameters]; the label is
   * provided via delegation.
   */
  @RADsl
  fun symbolWithNames(vararg parameters: String): GenericDelegatedProvider<LabeledSymbol<String>> =
      GenericDelegatedProvider {
        LabeledSymbol(it, parameters.map(::Parameter))
      }

  /** Create a new symbol with the given label [name] and parameter names [parameters]. */
  @RADsl
  fun symbolWithNames(name: String, vararg parameters: String): LabeledSymbol<String> =
      LabeledSymbol(name, parameters.map(::Parameter))

  /**
   * Property that adds a new symbol with the given number of parameters [arity] (named `p0`, `p1`
   * etc.); the label is provided via delegation.
   */
  @RADsl
  fun symbolOfArity(arity: Int): GenericDelegatedProvider<LabeledSymbol<String>> =
      GenericDelegatedProvider {
        LabeledSymbol(it, List(arity) { p -> Parameter("p$p") })
      }

  /**
   * Create a new symbol with the given label [name] and number of parameters [arity] (named `p0`,
   * `p1` etc.) .
   */
  @RADsl
  fun symbolOfArity(name: String, arity: Int): LabeledSymbol<String> =
      LabeledSymbol(name, List(arity) { p -> Parameter("p$p") })

  /**
   * Property that adds a new location. [accepting] is true `iff` the location should accept; the
   * name is provided via delegation.
   */
  @RADsl
  fun location(
      accepting: Boolean = false
  ): GenericDelegatedProvider<Location<Label, Alphabet, GuardTheory>> = GenericDelegatedProvider {
    addLocation(it, accepting)
  }

  /**
   * Add a location to the RA under construction with the given [name] and acceptance determined by
   * [accepting].
   */
  @RADsl
  fun location(name: String, accepting: Boolean = false): Location<Label, Alphabet, GuardTheory> =
      ra.addLocation(name, accepting)

  /** Property that adds a new register; the name is provided via delegation. */
  @RADsl
  fun register(): GenericDelegatedProvider<Register> = GenericDelegatedProvider { addRegister(it) }

  /** Add a register to the RA under construction with the given [name]. */
  @RADsl fun register(name: String): Register = ra.addRegister(name)

  /**
   * Property that adds a new register with the given initial value; the name is provided via
   * delegation.
   */
  @RADsl
  fun registerInitially(initialValue: Alphabet): GenericDelegatedProvider<Register> =
      GenericDelegatedProvider {
        addRegister(it, initialValue)
      }

  /** Add a register to the RA under construction with the given [name] and [initialValue]. */
  @RADsl
  fun registerInitially(name: String, initialValue: Alphabet): Register =
      ra.addRegister(name, initialValue)

  /**
   * Add a transition to the RA under construction.
   *
   * @param from the source location.
   * @param symbol the transition symbol
   * @param guard the guard for the transition.
   * @param assignment the assignment for the transition.
   * @param to the target location.
   */
  @RADsl
  fun transition(
      from: Location<Label, Alphabet, GuardTheory>,
      symbol: LabeledSymbol<Label>,
      guard: GuardTheory,
      assignment: Map<Register, Symbol>,
      to: Location<Label, Alphabet, GuardTheory>
  ) = ra.addTransition(from, symbol, guard, assignment, to)

  /** Utility tuple for a more natural assignment syntax ([target] [from] [source]). */
  @RADsl data class Assignment(val target: Register, val source: Symbol)

  /**
   * Construct an assignment map (target to source) from the given [Assignment] tuples
   * [assignments].
   */
  @RADsl
  fun assignments(vararg assignments: Assignment) =
      assignments.associate { (target, source) -> target to source }

  /** Natural syntax to construct assignment tuples (target from [source]). */
  @RADsl infix fun Register.from(source: Symbol): Assignment = Assignment(this, source)

  /** Extract the register automaton under construction from the context. */
  fun build(): RegisterAutomaton<Label, Alphabet, GuardTheory> = ra
}
