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

package tools.aqua.rage.commands

import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.io.path.Path
import kotlin.random.Random
import tools.aqua.rage.generator.champarnaudParanthoenRA
import tools.aqua.rage.generator.concat
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.ig.InequalityGuard
import tools.aqua.rage.model.ig.True
import tools.aqua.rage.util.createCallableVariants
import tools.aqua.rage.util.toIntRange

/** Subcommand for generating a DFA-then-RA-then-DFA strcuture. */
object DfaRaDfa :
    GenerationCommand(name = "dfa-ra-dfa", help = "Generate a DFA-Gadget-DFA structure") {

  private val outputPath by option("-o", "--output").path(canBeFile = false).default(Path("."))
  private val force by option("-f", "--force").flag("--no-force")
  private val legacyNames by option("-l", "--legacy").flag("--unique")
  private val dfaLocations by
      option("-Q", "--dfa-locations")
          .convert { it.toIntRange() }
          .default(5..100 step 5)
          .check { it.first > 0 }
  private val dfaAlphabetSize by
      option("-A", "--dfa-alphabet-size")
          .convert { it.toIntRange() }
          .default(2..10)
          .check { it.first > 0 }
  private val nGadgets by
      option("-G", "--gadgets").convert { it.toIntRange() }.default(1..6).check { it.first > 0 }
  private val labels by option("-L", "--labels").int().default(6).check { it >= nGadgets.last }
  private val seeds by option("-S", "--seeds").convert { it.toIntRange() }.default(1..100)

  override fun createTasks(): List<Callable<Path>> =
      createCallableVariants(
          dfaLocations.toList(),
          dfaAlphabetSize.toList(),
          nGadgets.toList(),
          seeds.toList(),
          ::generateDfaRaDfa)

  private fun generateDfaRaDfa(locations: Int, alphabetSize: Int, gadgets: Int, seed: Int): Path =
      outputPath
          .resolveInPathAndName(
              "Q-$locations",
              "A-$alphabetSize",
              if (legacyNames) "G-$gadgets" else "G-$gadgets-$labels",
              final = "$seed.xml")
          .writeToNewIfMissing(force) {
            createAutomatonConcat(seed, locations, alphabetSize, 1, gadgets)
                .toWikiWithBonus(maxGeneratedGadgets = labels, nGeneratedGadgets = gadgets)
                .toRALibString()
          }

  private fun createAutomatonConcat(
      seed: Int,
      dfaLocations: Int,
      alphabetSize: Int,
      nDFAParameters: Int = 0,
      nGadgets: Int,
      nGadgetParameters: Int = 2
  ): RegisterAutomaton<String, Int, InequalityGuard<Int>> {
    val alphabet = createAlphabet(alphabetSize)

    val random = Random(seed)

    val dfa1 =
        champarnaudParanthoenRA<String, Int, InequalityGuard<Int>>(
            dfaLocations, alphabet, nDFAParameters, True(), random, locationPrefix = "dfa1_")
    val dfa2 =
        champarnaudParanthoenRA<String, Int, InequalityGuard<Int>>(
            dfaLocations, alphabet, nDFAParameters, True(), random, locationPrefix = "dfa2_")

    val gadgets = createGadgets(nGadgets, nGadgetParameters)

    return dfa1 concat gadgets.reduce { left, right -> left concat right } concat dfa2
  }
}
