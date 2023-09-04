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

import tools.aqua.rage.model.DefaultRegisterAutomaton
import tools.aqua.rage.model.LabeledSymbol
import tools.aqua.rage.model.Location
import tools.aqua.rage.model.Parameter
import tools.aqua.rage.model.Register
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.Symbol
import tools.aqua.rage.model.Transition
import tools.aqua.rage.model.ig.*
import tools.aqua.rage.model.ig.And
import tools.aqua.rage.model.ig.True
import tools.aqua.rage.wiki.Alphabet as WikiAlphabet
import tools.aqua.rage.wiki.And as WikiAnd
import tools.aqua.rage.wiki.Equal as WikiEqual
import tools.aqua.rage.wiki.Greater as WikiGreater
import tools.aqua.rage.wiki.GreaterEqual as WikiGreaterEqual
import tools.aqua.rage.wiki.Less as WikiLess
import tools.aqua.rage.wiki.LessEqual as WikiLessEqual
import tools.aqua.rage.wiki.Location as WikiLocation
import tools.aqua.rage.wiki.NotEqual as WikiNotEqual
import tools.aqua.rage.wiki.Or as WikiOr
import tools.aqua.rage.wiki.Parameter as WikiParameter
import tools.aqua.rage.wiki.Register as WikiRegister
import tools.aqua.rage.wiki.Symbol as WikiSymbol
import tools.aqua.rage.wiki.Transition as WikiTransition
import tools.aqua.rage.wiki.True as WikiTrue
import tools.aqua.rage.wiki.Type.INT

/**
 * Convert this Automata Wiki RA into the internal representation. No locations are set to
 * accepting, inputs and outputs are merged, constants become regular initialized registers,
 * parameter renamings are removed and constants in guards replaced with references to constant
 * registers.
 */
fun WikiRegisterAutomaton.toModel(): RegisterAutomaton<String, Int, InequalityGuard<Int>> {
  val alphabetNameToSymbol =
      (alphabet.inputs + alphabet.outputs).associate { a ->
        a.name to LabeledSymbol(a.name, a.params.map { Parameter(it.name) })
      }

  val initial = locations.single { it.initial }
  return DefaultRegisterAutomaton<String, Int, InequalityGuard<Int>>(initial.name, false).also { ra
    ->
    val registerToNew =
        (constants + globals).associateWith { ra.addRegister(it.name, it.value.toInt()) }
    val nameToRegister = ra.registers.associateBy(Register::name)
    val constantToRegister =
        constants.map(registerToNew::getValue).associateBy(ra.initialValuation::getValue)

    val locationsToNew =
        locations
            .filterNot { it === initial }
            .associate { it.name to ra.addLocation(it.name, false) } +
            mapOf(initial.name to ra.initialLocation)

    transitions.forEach { t ->
      val labeledSymbol = alphabetNameToSymbol.getValue(t.symbol)
      val nameToSymbol = (t.params zip labeledSymbol.parameters).toMap() + nameToRegister
      ra.addTransition(
          from = locationsToNew.getValue(t.from),
          symbol = labeledSymbol,
          guard = t.guard.toModel(nameToSymbol, constantToRegister),
          assignment = t.assignments.associate { it.toPair(nameToSymbol, constantToRegister) },
          to = locationsToNew.getValue(t.to))
    }
  }
}

private fun Guard.toModel(
    nameToSymbol: Map<String, Symbol>,
    constantToRegister: Map<Int, Register>
): InequalityGuard<Int> =
    when (this) {
      WikiTrue -> True()
      is WikiAnd -> And(conjuncts.map { it.toModel(nameToSymbol, constantToRegister) })
      is WikiOr -> Or(disjuncts.map { it.toModel(nameToSymbol, constantToRegister) })
      is WikiEqual ->
          Equals(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
      is WikiNotEqual ->
          NotEquals(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
      is WikiGreaterEqual ->
          GreaterEquals(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
      is WikiGreater ->
          Greater(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
      is WikiLessEqual ->
          LessEquals(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
      is WikiLess ->
          Less(
              left.toModel(nameToSymbol, constantToRegister),
              right.toModel(nameToSymbol, constantToRegister))
    }

private fun Assignment.toPair(
    nameToSymbol: Map<String, Symbol>,
    constantToRegister: Map<Int, Register>
): Pair<Register, Symbol> =
    nameToSymbol.getValue(to) as Register to from.toModel(nameToSymbol, constantToRegister)

private fun Expression.toModel(
    nameToSymbol: Map<String, Symbol>,
    constantToRegister: Map<Int, Register>
): Symbol =
    when (this) {
      is Variable -> nameToSymbol.getValue(this.identifier)
      is Constant -> constantToRegister.getValue(this.value)
    }

/**
 * Convert this RA to an Automata Wiki compliant RA. Each transition is split to include an input
 * and output action. For accepting states, entering transitions output `OAccept`, else `OReject`.
 * Missing transitions are redirected to a sink state that only outputs `OError`. The input alphabet
 * is determined from the used symbols plus [additionalSymbols].
 */
fun RegisterAutomaton<String, Int, InequalityGuard<Int>>.toWiki(
    additionalSymbols: Collection<LabeledSymbol<String>> = emptySet()
): WikiRegisterAutomaton {
  val symbols =
      transitions.mapTo(mutableSetOf(), Transition<String, Int, InequalityGuard<Int>>::symbol) +
          additionalSymbols
  val alphabet =
      WikiAlphabet(
          inputs = symbols.map(LabeledSymbol<String>::toWiki),
          outputs = listOf(accept, reject, error))

  val registers = registers.map(Register::toWiki)

  val transitionToIO =
      transitions.withIndex().associate { (id, transition) ->
        transition to transition.toWikiIO(id)
      }
  val inputLocationsToOriginal = locations.associateBy { it.toWiki() } + mapOf(trap to null)
  val outputLocations = transitionToIO.values + trapIO

  val transitions =
      transitions.flatMap { it.toWikiWithIO(transitionToIO) } +
          inputLocationsToOriginal.flatMap { (location, original) ->
            (original?.outgoing ?: emptySet()).toWikiMissing(location, symbols)
          } +
          trapTransition

  return WikiRegisterAutomaton(
      alphabet = alphabet,
      constants = emptyList(),
      globals = registers,
      locations = inputLocationsToOriginal.keys.toList() + outputLocations,
      transitions = transitions)
}

private val accept = WikiSymbol("OAccept", emptyList())
private val reject = WikiSymbol("OReject", emptyList())
private val error = WikiSymbol("OError", emptyList())

private fun LabeledSymbol<String>.toWiki() =
    WikiSymbol(
        "I$label",
        parameters.let { params -> params.map { WikiParameter(name = it.name, type = INT) } })

private fun Register.toWiki() = WikiRegister(name = name, type = INT, value = "0")

private fun Location<String, Int, InequalityGuard<Int>>.toWiki(): WikiLocation =
    WikiLocation(initial = isInitial, name = "l_$name")

private fun Transition<String, Int, InequalityGuard<Int>>.toWikiIO(id: Int): WikiLocation =
    WikiLocation(initial = false, name = "io_${id}_${from.name}_${symbol.label}_${to.name}")

private val trap = WikiLocation(initial = false, name = "trap")
private val trapIO = WikiLocation(initial = false, name = "io_trap")

private fun Transition<String, Int, InequalityGuard<Int>>.toWikiWithIO(
    transitionToIO: Map<Transition<String, Int, InequalityGuard<Int>>, WikiLocation>
): Collection<WikiTransition> {
  val symbol = symbol.toWiki()

  return listOf(
      WikiTransition(
          from = from.toWiki().name,
          symbol = symbol.name,
          params = symbol.params.map(WikiParameter::name),
          to = transitionToIO.getValue(this).name,
          guard = guard.toWiki(),
          assignments = assignment.toWiki()),
      WikiTransition(
          from = transitionToIO.getValue(this).name,
          symbol = (if (to.isAccepting) accept else reject).name,
          params = (if (to.isAccepting) accept else reject).params.map(WikiParameter::name),
          to = to.toWiki().name,
          guard = WikiTrue,
          assignments = emptyList(),
      ),
  )
}

private fun Set<Transition<String, Int, InequalityGuard<Int>>>.toWikiMissing(
    from: WikiLocation,
    alphabet: Set<LabeledSymbol<String>>
): Collection<WikiTransition> = alphabet.mapNotNull { toWikiMissing(from, it) }

private fun Set<Transition<String, Int, InequalityGuard<Int>>>.toWikiMissing(
    from: WikiLocation,
    symbol: LabeledSymbol<String>
): WikiTransition? {
  val matchingSymbol = filter { it.symbol == symbol }
  val symbolWiki = symbol.toWiki()
  return when {
    matchingSymbol.isEmpty() ->
        WikiTransition(
            from = from.name,
            symbol = symbolWiki.name,
            params = symbolWiki.params.map(WikiParameter::name),
            to = trapIO.name,
            guard = WikiTrue,
            assignments = emptyList())
    matchingSymbol.all { it.guard is True } -> null
    else ->
        WikiTransition(
            from = from.name,
            symbol = symbolWiki.name,
            params = symbolWiki.params.map(WikiParameter::name),
            to = trapIO.name,
            guard = And(matchingSymbol.map { it.guard }).invert().toWiki(),
            assignments = emptyList())
  }
}

private val trapTransition =
    WikiTransition(
        from = trapIO.name,
        symbol = error.name,
        params = error.params.map(WikiParameter::name),
        to = trap.name,
        guard = WikiTrue,
        assignments = emptyList())

private fun InequalityGuard<Int>.toWiki(): Guard =
    when (this) {
      is True -> WikiTrue
      is And -> ComplexAnd(conjuncts.map { it.toWiki() })
      is Or -> ComplexOr(disjuncts.map { it.toWiki() })
      is Equals -> WikiEqual(Variable(left.name), Variable(right.name))
      is NotEquals -> WikiNotEqual(Variable(left.name), Variable(right.name))
      is GreaterEquals -> WikiGreaterEqual(Variable(left.name), Variable(right.name))
      is Greater -> WikiGreater(Variable(left.name), Variable(right.name))
      is LessEquals -> WikiLessEqual(Variable(left.name), Variable(right.name))
      is Less -> WikiLess(Variable(left.name), Variable(right.name))
    }

private fun Map<Register, Symbol>.toWiki(): List<Assignment> =
    entries.map { (to, from) -> Assignment(to = to.name, from = Variable(from.name)) }
