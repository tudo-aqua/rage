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

import java.io.BufferedReader
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import nl.adaptivity.xmlutil.serialization.XML
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.xmlunit.assertj3.XmlAssert.assertThat as assertThatXML
import tools.aqua.rage.wiki.Type.INT

@TestInstance(PER_CLASS)
class SerializationTests {

  private val simpleRA =
      WikiRegisterAutomaton(
          Alphabet(
              inputs =
                  listOf(
                      Symbol("I_simple", params = emptyList()),
                      Symbol(
                          "I_parameterized",
                          params = listOf(Parameter("pi_0", INT), Parameter("pi_1", INT)))),
              outputs =
                  listOf(
                      Symbol("O_simple", params = emptyList()),
                      Symbol(
                          "O_parameterized",
                          params = listOf(Parameter("po_0", INT), Parameter("po_1", INT))))),
          constants = listOf(Register("C_1000", INT, value = "1000")),
          globals = listOf(Register("x_0", INT, value = "0"), Register("x_1", INT, value = "0")),
          locations =
              listOf(
                  Location(initial = true, "q_0"),
                  Location(initial = false, "q_1"),
                  Location(initial = false, "q_2"),
                  Location(initial = false, "q_3"),
              ),
          transitions =
              listOf(
                  Transition(
                      from = "q_0",
                      params = emptyList(),
                      symbol = "I_simple",
                      to = "q_1",
                      guard = True,
                      assignments = emptyList()),
                  Transition(
                      from = "q_0",
                      params = listOf("a", "b"),
                      symbol = "I_parameterized",
                      to = "q_1",
                      guard =
                          ComplexOr(
                              listOf(
                                  ComplexAnd(
                                      listOf(
                                          Equal(Variable("a"), Variable("b")),
                                          NotEqual(Variable("a"), Constant(1000)),
                                      )),
                                  GreaterEqual(Variable("a"), Variable("x_0")))),
                      assignments =
                          listOf(
                              Assignment(to = "x_0", from = Variable("a")),
                              Assignment(to = "x_1", from = Constant(1000)))),
                  Transition(
                      from = "q_1",
                      params = listOf("x", "y"),
                      symbol = "O_parameterized",
                      to = "q_2",
                      guard = Less(Variable("x"), Variable("y")),
                      assignments = emptyList()),
                  Transition(
                      from = "q_2",
                      params = emptyList(),
                      symbol = "O_simple",
                      to = "q_3",
                      guard = True,
                      assignments = emptyList()),
              ))

  private val raLibFormat = XML(SerializersModule { contextual(AutomataWikiGuardSerializer) })

  @Test
  fun `test that simple automaton deserializes correctly`() {
    val simpleRAXML =
        javaClass
            .getResourceAsStream("/simple-redundant.register.xml")!!
            .bufferedReader()
            .use(BufferedReader::readText)

    val ra = raLibFormat.decodeFromString<WikiRegisterAutomaton>(simpleRAXML)

    assertThat(ra).isEqualTo(simpleRA)
  }

  @Test
  fun `test that simple automaton serializes correctly`() {
    val simpleRAXML =
        javaClass
            .getResourceAsStream("/simple-canonical.register.xml")!!
            .bufferedReader()
            .use(BufferedReader::readText)

    val raXML = raLibFormat.encodeToString(simpleRA)
    println(raXML)

    assertThatXML(raXML).and(simpleRAXML).ignoreComments().ignoreWhitespace().areIdentical()
  }
}
