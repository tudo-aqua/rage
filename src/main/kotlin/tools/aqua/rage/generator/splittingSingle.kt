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

import kotlin.random.Random
import tools.aqua.rage.model.DefaultRegisterAutomaton
import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.Location
import tools.aqua.rage.model.Register
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.Symbol
import tools.aqua.rage.util.bucketed

fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
/**
 * In this register automaton, split one location and include the [discriminator].
 *
 * The operation identifies a [random] location and splits it into four. The incoming transitions
 * are [random]ly split equally between two of the new locations, the outcoming between the other
 * two. Loops are removed. Then, each incoming-outgoing split location pair is connected via the
 * [discriminator]. This replaces the discriminator's initial location with the incoming split
 * location and the first terminal (see [findFirstTerminal]) with the outgoing split location.
 */
.splitSingle(
    discriminator: RegisterAutomaton<Label, Alphabet, GuardTheory>,
    random: Random
): RegisterAutomaton<Label, Alphabet, GuardTheory> {
  val locationToSplit =
      locations
          .filterNot {
            it.isInitial ||
                it.isAccepting ||
                (it.incoming - it.loops).size < 2 ||
                (it.outgoing - it.loops).size < 2
          }
          .random(random)

  require(discriminator.initialValuation.isEmpty()) {
    "initialized registers : ${discriminator.initialValuation.keys}"
  }
  val discriminatorTerminal = discriminator.findFirstTerminal()

  fun translateName(location: Location<Label, Alphabet, GuardTheory>): String = "l_${location.name}"

  val ra =
      DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>(
          initialLocationName = translateName(initialLocation),
          initialLocationIsAccepting = initialLocation.isAccepting)

  val locationsToNew =
      (locations - initialLocation - locationToSplit).associateWith { l ->
        ra.addLocation(name = translateName(l), isAccepting = l.isAccepting)
      } + (initialLocation to ra.initialLocation)

  val splitIncomingLeft = ra.addLocation("lil_${locationToSplit.name}", locationToSplit.isAccepting)
  val splitIncomingRight =
      ra.addLocation("lir_${locationToSplit.name}", locationToSplit.isAccepting)
  val splitOutgoingLeft = ra.addLocation("lol_${locationToSplit.name}", locationToSplit.isAccepting)
  val splitOutgoingRight =
      ra.addLocation("lor_${locationToSplit.name}", locationToSplit.isAccepting)

  val registersToNew =
      initialValuation.entries.associate { (register, valuation) ->
        register to ra.addRegister(register.name, valuation)
      } +
          ((registers + discriminator.registers) - initialValuation.keys).toSet().associateWith {
            ra.addRegister(it.name)
          }

  fun translateRegister(register: Register): Register = registersToNew.getValue(register)

  fun translateSymbol(symbol: Symbol): Symbol =
      (symbol as? Register)?.let(::translateRegister) ?: symbol

  (transitions - locationToSplit.incoming - locationToSplit.outgoing).forEach { t ->
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

  val (incomingLeft, incomingRight) =
      locationToSplit.incoming.filter { it.from != it.to }.shuffled(random).bucketed(2)
  val (outgoingLeft, outgoingRight) =
      locationToSplit.outgoing.filter { it.from != it.to }.shuffled(random).bucketed(2)

  incomingLeft.forEach { t ->
    ra.addTransition(
        from = locationsToNew.getValue(t.from),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = splitIncomingLeft)
  }

  incomingRight.forEach { t ->
    ra.addTransition(
        from = locationsToNew.getValue(t.from),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = splitIncomingRight)
  }

  outgoingLeft.forEach { t ->
    ra.addTransition(
        from = splitOutgoingLeft,
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = locationsToNew.getValue(t.to))
  }

  outgoingRight.forEach { t ->
    ra.addTransition(
        from = splitOutgoingRight,
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = locationsToNew.getValue(t.to))
  }

  val leftLocationsToNew =
      (discriminator.locations - discriminator.initialLocation - discriminatorTerminal)
          .associateWith { l ->
            ra.addLocation(name = "dl_${l.name}", isAccepting = l.isAccepting)
          } +
          mapOf(
              discriminator.initialLocation to splitIncomingLeft,
              discriminatorTerminal to splitOutgoingLeft)

  discriminator.transitions.forEach { t ->
    ra.addTransition(
        from = leftLocationsToNew.getValue(t.from),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = leftLocationsToNew.getValue(t.to))
  }

  val rightLocationsToNew =
      (discriminator.locations - discriminator.initialLocation - discriminatorTerminal)
          .associateWith { l ->
            ra.addLocation(name = "dr_${l.name}", isAccepting = l.isAccepting)
          } +
          mapOf(
              discriminator.initialLocation to splitIncomingRight,
              discriminatorTerminal to splitOutgoingRight)

  discriminator.transitions.forEach { t ->
    ra.addTransition(
        from = rightLocationsToNew.getValue(t.from),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = rightLocationsToNew.getValue(t.to))
  }

  return ra
}
