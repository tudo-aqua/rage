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
import tools.aqua.rage.model.Transition
import tools.aqua.rage.util.bucketed

/**
 * In this register automaton, replace some transitions with the given [replacements].
 *
 * The operation identifies a [random] maximal set of transitions that do not connect on any
 * location (i.e., an independent set in the dual graph). Then, a [share] of these is [random]ly
 * selected and matched with the [replacements] in equal number. Finally, each selected transition
 * is replaced with its element of the [replacements]. This replaces the replacement's initial
 * location with the transition source and the first terminal (see [findFirstTerminal]) with the
 * target.
 */
fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
    .partialReplacement(
    share: Double,
    replacements: List<RegisterAutomaton<Label, Alphabet, GuardTheory>>,
    random: Random
): RegisterAutomaton<Label, Alphabet, GuardTheory> {
  require(share in 0.0..1.0)
  val eligibleTransitions = transitions.toMutableSet()
  val transitionCandidates = mutableSetOf<Transition<Label, Alphabet, GuardTheory>>()
  while (eligibleTransitions.isNotEmpty()) {
    val replace = eligibleTransitions.random(random)
    transitionCandidates += replace
    eligibleTransitions -=
        replace.from.incoming + replace.from.outgoing + replace.to.incoming + replace.to.outgoing
  }
  val transitionsToReplace =
      transitionCandidates
          .shuffled(random)
          .take((transitionCandidates.size * share).toInt())
          .toSet()

  require(replacements.isNotEmpty())
  val transitionToIndexAndReplacement =
      ((transitionsToReplace.withIndex().toList().bucketed(replacements.size)) zip replacements)
          .flatMap { (transitionsIndexed, replacement) ->
            transitionsIndexed.map { (index, transition) -> transition to (index to replacement) }
          }
          .toMap()

  val replacementsTerminal =
      replacements.associateWith { replacement ->
        require(replacement.initialValuation.isEmpty()) {
          "initialized registers : ${replacement.initialValuation.keys}"
        }
        replacement.findFirstTerminal()
      }

  fun translateName(location: Location<Label, Alphabet, GuardTheory>): String {
    var name = "l_${location.name}"
    location.outgoing
        .mapNotNull { transitionToIndexAndReplacement[it] }
        .singleOrNull()
        ?.also { (idx, replacement) -> name += "+i${idx}_${replacement.initialLocation.name}" }
    location.incoming
        .mapNotNull { transitionToIndexAndReplacement[it] }
        .singleOrNull()
        ?.also { (idx, replacement) ->
          name += "+i${idx}_${replacementsTerminal.getValue(replacement).name}"
        }
    return name
  }

  val ra =
      DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>(
          initialLocationName = translateName(initialLocation),
          initialLocationIsAccepting = initialLocation.isAccepting)
  val outerLocationsToNew =
      (locations - initialLocation).associateWith { l ->
        ra.addLocation(name = translateName(l), isAccepting = l.isAccepting)
      } + (initialLocation to ra.initialLocation)

  val registersToNew =
      initialValuation.entries.associate { (register, valuation) ->
        register to ra.addRegister(register.name, valuation)
      } +
          ((registers + replacements.flatMap { it.registers }) - (initialValuation.keys))
              .toSet()
              .associateWith { ra.addRegister(it.name) }

  fun translateRegister(register: Register): Register = registersToNew.getValue(register)

  fun translateSymbol(symbol: Symbol): Symbol =
      (symbol as? Register)?.let(::translateRegister) ?: symbol

  (transitions - transitionsToReplace).forEach { t ->
    ra.addTransition(
        from = outerLocationsToNew.getValue((t.from)),
        symbol = t.symbol,
        guard = t.guard,
        assignment =
            t.assignment.entries.associate { (target, source) ->
              translateRegister(target) to translateSymbol(source)
            },
        to = outerLocationsToNew.getValue(t.to))
  }

  transitionToIndexAndReplacement.entries.forEach { (replaced, indexAndReplacement) ->
    val (idx, replacement) = indexAndReplacement
    val replacementTerminal = replacementsTerminal.getValue(replacement)
    val innerLocationsToNew =
        (replacement.locations - replacement.initialLocation - replacementTerminal).associateWith {
            l ->
          ra.addLocation(name = "i${idx}_${l.name}", isAccepting = l.isAccepting)
        } +
            mapOf(
                replacement.initialLocation to outerLocationsToNew.getValue(replaced.from),
                replacementTerminal to outerLocationsToNew.getValue(replaced.to))

    replacement.transitions.forEach { t ->
      ra.addTransition(
          from = innerLocationsToNew.getValue(t.from),
          symbol = t.symbol,
          guard = t.guard,
          assignment =
              t.assignment.entries.associate { (target, source) ->
                translateRegister(target) to translateSymbol(source)
              },
          to = innerLocationsToNew.getValue(t.to))
    }
  }

  return ra
}
