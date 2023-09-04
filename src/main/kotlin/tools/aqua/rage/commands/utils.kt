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

import java.lang.ProcessHandle.current as currentProcess
import java.nio.charset.Charset
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import kotlin.io.path.createDirectories
import kotlin.io.path.moveTo
import kotlin.io.path.notExists
import kotlin.io.path.writeText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import nl.adaptivity.xmlutil.serialization.XML
import tools.aqua.rage.model.LabeledSymbol
import tools.aqua.rage.model.Parameter
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.ig.And
import tools.aqua.rage.model.ig.Equals
import tools.aqua.rage.model.ig.InequalityGuard
import tools.aqua.rage.model.ig.True
import tools.aqua.rage.model.ra
import tools.aqua.rage.util.toString
import tools.aqua.rage.wiki.RALibGuardSerializer
import tools.aqua.rage.wiki.WikiRegisterAutomaton
import tools.aqua.rage.wiki.toWiki

internal val raLibFormat =
    XML(SerializersModule { contextual(RALibGuardSerializer) }) { indentString = "  " }

private val lowercaseAlphabet = ('a'..'z').map { it.toString() }
private val uppercaseAlphabet = ('A'..'Z').map { it.toString() }

internal fun createAlphabet(alphabetSize: Int): List<String> =
    List(alphabetSize) { it.toString(lowercaseAlphabet) }

internal fun createGadgets(
    nGadgets: Int,
    gadgetParameters: Int
): List<RegisterAutomaton<String, Int, InequalityGuard<Int>>> {
  val parameters = List(gadgetParameters) { Parameter("p$it") }
  val labels = List(nGadgets) { LabeledSymbol(it.toString(uppercaseAlphabet), parameters) }
  return labels.withIndex().map { (i, label) -> gadget(label, "ra${i}_") }
}

internal fun gadget(symbol: LabeledSymbol<String>, prefix: String, repetitions: Int = 1) =
    ra<String, Int, InequalityGuard<Int>>("${prefix}_l0") {
      val otherLocations =
          (1..repetitions + 1).map { location("${prefix}_l$it", it == repetitions + 1) }

      val registers = (0..symbol.parameters.size).map { register("${prefix}_x$it") }

      transition(
          initial,
          symbol,
          True(),
          (registers zip symbol.parameters).toMap(),
          otherLocations.first())
      otherLocations.zipWithNext().forEach { (from, to) ->
        transition(
            from,
            symbol,
            And((registers zip symbol.parameters).map { (r, p) -> Equals(r, p) }),
            assignments(),
            to)
      }
    }

internal fun RegisterAutomaton<String, Int, InequalityGuard<Int>>.toWikiWithBonus(
    maxGeneratedGadgets: Int,
    nGeneratedGadgets: Int,
    nParameters: Int = 2,
): WikiRegisterAutomaton {
  val parameters = List(nParameters) { Parameter("p$it") }
  val bonusLabels =
      List(maxGeneratedGadgets - nGeneratedGadgets) {
        LabeledSymbol((it + nGeneratedGadgets).toString(uppercaseAlphabet), parameters)
      }

  return toWiki(bonusLabels)
}

internal fun WikiRegisterAutomaton.toRALibString() = raLibFormat.encodeToString(this)

internal fun Path.resolveInPathAndName(vararg components: String, final: String) =
    resolve(
        components.joinToString("/", postfix = "/") +
            components.joinToString("_", postfix = "_$final"))

internal fun Path.writeTextAtomic(
    text: CharSequence,
    charset: Charset = Charsets.UTF_8,
    vararg options: OpenOption
) {
  resolveSibling("~${fileName}.${currentProcess().pid()}").also {
    it.writeText(text, charset, *options)
    it.moveTo(this, REPLACE_EXISTING, ATOMIC_MOVE)
  }
}

internal fun CharSequence.writeToNew(path: Path): Path =
    path.also {
      it.parent.createDirectories()
      it.writeTextAtomic(this)
    }

internal fun Path.writeToNewIfMissing(force: Boolean, generator: () -> String): Path {
  if (force || this.notExists()) {
    generator().writeToNew(this)
  }
  return this
}
