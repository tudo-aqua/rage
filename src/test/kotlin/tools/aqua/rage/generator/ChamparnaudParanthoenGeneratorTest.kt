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

import java.math.BigInteger
import java.util.function.Function
import kotlin.random.Random
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import tools.aqua.rage.model.tg.TrivialGuard
import tools.aqua.rage.model.tg.True
import tools.aqua.rage.util.ceilDiv
import tools.aqua.rage.util.reach

@TestInstance(PER_CLASS)
class ChamparnaudParanthoenGeneratorTest {

  private val table1 =
      listOf(
              listOf("1", "3", "6", "10", "15", "21", "28", "36"),
              listOf("1", "7", "25", "65", "140", "266", "462", "750"),
              listOf("0", "14", "89", "349", "1049", "2645", "5879", "11879"),
              listOf("0", "28", "295", "1691", "6936", "22806", "63959", "158991"),
              listOf("0", "0", "885", "7649", "42329", "179165", "626878", "1898806"),
              listOf("0", "0", "2655", "33251", "244896", "1319886", "5708032", "20898480"),
              listOf("0", "0", "0", "133004", "1357484", "9276800", "49233024", "216420864"),
              listOf("0", "0", "0", "532016", "7319436", "62980236", "407611404", "2138978316"),
              listOf("0", "0", "0", "0", "36597180", "414478596", "3267758424", "20379584952"),
              listOf("0", "0", "0", "0", "182985900", "2669857476", "25544166444", "188580846060"),
              listOf("0", "0", "0", "0", "0", "16019144856", "194828309964", "1703475078444"),
              listOf("0", "0", "0", "0", "0", "96114869136", "1459913038884", "15087713666436"),
              listOf("0", "0", "0", "0", "0", "0", "10219391272188", "130921100603676"),
              listOf("0", "0", "0", "0", "0", "0", "71535738905316", "1118904543734724"),
              listOf("0", "0", "0", "0", "0", "0", "0", "8951236349877792"),
              listOf("0", "0", "0", "0", "0", "0", "0", "71609890799022336"),
          )
          .map { it.map(::BigInteger) }

  @Test
  fun `check T table matches Table 1`() {
    val aT = arrayOfTheC(3, 16, 8)

    val result = (1..16).map { t -> (1..8).map { p -> aT.getValue(t to p) } }

    assertThat(result).isEqualTo(table1)
  }

  @ParameterizedTest
  @MethodSource("randomElementOfKTestSource")
  fun `check random tuples are of correct form`(m: Int, t: Int, p: Int, seed: Int) {
    val result = randomElementOfK(m, t, p, Random(seed))
    assertThat(result)
        .hasSize(if (p < t ceilDiv (m - 1)) 0 else t)
        .allSatisfy { it in 1..p }
        .isSorted
  }

  private fun randomElementOfKTestSource(): Iterator<Arguments> =
      sequence {
            for (m in 2..10 step 2) {
              for (t in 1..19 step 2) {
                for (p in 1..9 step 2) {
                  for (seed in 1..10) {
                    yield(Arguments.of(m, t, p, seed))
                  }
                }
              }
            }
          }
          .iterator()

  private val figure2 = tree {
    node("a") {
      node("a") {
        leaf("a")
        leaf("b")
        leaf("c")
      }
      leaf("b")
      leaf("c")
    }
    node("b") {
      leaf("a")
      leaf("b")
      leaf("c")
    }
    leaf("c")
  }

  @Test
  fun `check translation matches Figure 2`() {
    val result = phiInverse(listOf(3, 3, 3, 3, 3, 4, 4, 4), listOf("a", "b", "c"))

    assertThat(result).isEqualTo(figure2)
  }

  @ParameterizedTest
  @MethodSource("generationDataTestSource")
  fun `check ra is of correct form`(nStates: Int, alphabet: List<String>, seed: Int) {
    val ra =
        champarnaudParanthoenRA<String, Int, TrivialGuard<Int>>(
            nStates,
            alphabet,
            0,
            True(),
            Random(seed),
        )

    assertThat(ra.locations).hasSize(nStates)
    assertThat(ra.locations).allSatisfy { l ->
      assertThat(l.outgoing).map(Function { it.symbol.label }).hasSameElementsAs(alphabet)
    }
    assertThat(reach(ra.initialLocation) { l -> l.outgoing.map { it.to } })
        .hasSameElementsAs(ra.locations)

    assertThat(ra.registers).isEmpty()

    assertThat(ra.transitions).allSatisfy { t ->
      assertThat(t.assignment).isEmpty()
      assertThat(t.guard).isEqualTo(True<Int>())
    }
  }

  private fun generationDataTestSource(): Iterator<Arguments> =
      sequence {
            for (nStates in 2..20 step 2) {
              for (lastLetter in 'c'..'g') {
                for (seed in 1..10) {
                  yield(Arguments.of(nStates, ('a'..lastLetter).map { it.toString() }, seed))
                }
              }
            }
          }
          .iterator()
}
