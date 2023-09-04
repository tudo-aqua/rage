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
import com.github.ajalt.clikt.parameters.types.path
import java.nio.file.Path
import java.util.concurrent.Callable
import kotlin.io.path.Path
import kotlin.random.Random
import tools.aqua.rage.generator.champarnaudParanthoenRA
import tools.aqua.rage.generator.splitSingle
import tools.aqua.rage.model.LabeledSymbol
import tools.aqua.rage.model.Parameter
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.ig.InequalityGuard
import tools.aqua.rage.model.ig.True
import tools.aqua.rage.util.createCallableVariants
import tools.aqua.rage.util.toIntRange
import tools.aqua.rage.wiki.toWiki

/**
 * Command object for creating RAs from a DFA in which a single location is split and discriminating
 * suffixes are obscured by a configurable RA.
 */
object DfaSingleDiscriminator :
    GenerationCommand(
        name = "dfa-single-discriminator",
        help = "Generate a DFA structure tha one node and adds a discriminating RA sequence") {

  private val outputPath by option("-o", "--output").path(canBeFile = false).default(Path("."))
  private val force by option("-f", "--force").flag("--no-force")
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
  private val gadgetLength by
      option("-L", "--gadget-length")
          .convert { it.toIntRange() }
          .default(1..6)
          .check { it.first > 0 }
  private val seeds by option("-S", "--seeds").convert { it.toIntRange() }.default(1..100)

  override fun createTasks(): List<Callable<Path>> =
      createCallableVariants(dfaLocations, dfaAlphabetSize, gadgetLength, seeds, ::runGeneration)

  private fun runGeneration(
      locations: Int,
      alphabetSize: Int,
      gadgetLength: Int,
      seed: Int,
  ): Path =
      outputPath
          .resolveInPathAndName(
              "Q-$locations", "A-$alphabetSize", "L-$gadgetLength", final = "$seed.xml")
          .writeToNewIfMissing(force) {
            createAutomatonSplit(seed, locations, alphabetSize, 0, gadgetLength)
                .toWiki()
                .toRALibString()
          }

  private fun createAutomatonSplit(
      seed: Int,
      nLocations: Int,
      alphabetSize: Int,
      nDfaParameters: Int = 0,
      gadgetLength: Int,
      nGadgetParameters: Int = 2
  ): RegisterAutomaton<String, Int, InequalityGuard<Int>> {
    val alphabet = createAlphabet(alphabetSize)

    val random = Random(seed)

    val dfa =
        champarnaudParanthoenRA<String, Int, InequalityGuard<Int>>(
            nLocations, alphabet, nDfaParameters, True(), random, locationPrefix = "dfa_")

    val gadget =
        gadget(
            LabeledSymbol("A", List(nGadgetParameters) { Parameter("p$it") }),
            "ra",
            gadgetLength - 1)

    return dfa.splitSingle(gadget, random)
  }
}
