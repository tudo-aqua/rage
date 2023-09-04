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

package tools.aqua.rage.generator

import tools.aqua.rage.model.DefaultRegisterAutomaton
import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.Location
import tools.aqua.rage.model.Register
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.Symbol
import tools.aqua.rage.util.combine
import tools.aqua.rage.util.toInfiniteSequence

/**
 * Concatenate this register automaton with the [next].
 *
 * This replaces the next RA's initial location with this one's first terminal (see
 * [findFirstTerminal]).
 */
infix fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
    .concat(
    next: RegisterAutomaton<Label, Alphabet, GuardTheory>
): RegisterAutomaton<Label, Alphabet, GuardTheory> {
  val reinitializedRegisters = next.initialValuation.keys - registers
  require(reinitializedRegisters.isEmpty()) { "reinitialized registers : $reinitializedRegisters" }

  val joinPoint = findFirstTerminal()

  fun translateName(location: Location<Label, Alphabet, GuardTheory>) =
      if (location.isAccepting) {
        "l_${location.name}+r_${next.initialLocation.name}"
      } else {
        "l_${location.name}"
      }

  val ra =
      DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>(
          initialLocationName = translateName(initialLocation),
          initialLocationIsAccepting = initialLocation.isAccepting)
  val locationsToNew =
      (locations - initialLocation).associateWithTo(mutableMapOf()) {
        ra.addLocation(name = translateName(it), isAccepting = it.isAccepting)
      }
  locationsToNew[initialLocation] = ra.initialLocation
  locationsToNew[next.initialLocation] = locationsToNew.getValue(joinPoint)
  locationsToNew +=
      (next.locations - next.initialLocation).associateWith {
        ra.addLocation("r_${it.name}", it.isAccepting)
      }

  val registersToNew =
      (initialValuation.entries + next.initialValuation.entries).associate { (register, valuation)
        ->
        register to ra.addRegister(register.name, valuation)
      } +
          ((registers + next.registers) - (initialValuation.keys + next.initialValuation.keys))
              .associateWith { ra.addRegister(it.name) }

  fun translateRegister(register: Register): Register = registersToNew.getValue(register)

  fun translateSymbol(symbol: Symbol): Symbol =
      (symbol as? Register)?.let(::translateRegister) ?: symbol

  (transitions + next.transitions).forEach { t ->
    ra.addTransition(
        from = locationsToNew.getValue(t.from),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = locationsToNew.getValue(t.to))
  }

  return ra
}

/**
 * Concatenate each register automaton in this sequence with the matching element of [next],
 * zip-style.
 */
operator fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> Sequence<
    RegisterAutomaton<Label, Alphabet, GuardTheory>>
    .times(
    next: Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>>
): Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>> =
    combine(next) { ra1, ra2 -> ra1 concat ra2 }

/** Concatenate each register automaton in this sequence with [next]. */
operator fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> Sequence<
    RegisterAutomaton<Label, Alphabet, GuardTheory>>
    .times(
    next: RegisterAutomaton<Label, Alphabet, GuardTheory>
): Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>> = this * next.toInfiniteSequence()

/** Concatenate each register automaton in this sequence with [next]. */
operator fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
    .times(
    next: Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>>
): Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>> = toInfiniteSequence() * next

/** Concatenate this register automaton with [next]. */
operator fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
    .times(
    next: RegisterAutomaton<Label, Alphabet, GuardTheory>
): Sequence<RegisterAutomaton<Label, Alphabet, GuardTheory>> =
    toInfiniteSequence() * next.toInfiniteSequence()
