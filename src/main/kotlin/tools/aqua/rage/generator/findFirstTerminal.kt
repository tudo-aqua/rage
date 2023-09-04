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

import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.Location
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.model.Transition
import tools.aqua.rage.util.reachWithDistance

/**
 * Find the first location in this automaton that is most distant from the initial location. This
 * performs reachability analysis and records the distance, then selects the first location with
 * maximum distance.
 */
fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> RegisterAutomaton<
    Label, Alphabet, GuardTheory>
    .findFirstTerminal(): Location<Label, Alphabet, GuardTheory> {
  val locations =
      reachWithDistance(initialLocation) {
        it.outgoing.map(Transition<Label, Alphabet, GuardTheory>::to)
      }
  val mostDistant = locations.filter { (_, distance) -> distance == locations.values.max() }
  return mostDistant.keys.first()
}
