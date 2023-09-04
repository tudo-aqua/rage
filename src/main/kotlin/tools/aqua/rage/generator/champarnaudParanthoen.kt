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
import java.math.BigInteger.ONE
import java.math.BigInteger.TWO
import java.math.BigInteger.ZERO
import kotlin.random.Random
import tools.aqua.rage.model.DefaultRegisterAutomaton
import tools.aqua.rage.model.Formula
import tools.aqua.rage.model.LabeledSymbol
import tools.aqua.rage.model.Parameter
import tools.aqua.rage.model.RegisterAutomaton
import tools.aqua.rage.util.ceilDiv
import tools.aqua.rage.util.nextBigInteger

/** The Catalan number computation in Fig. 4. */
internal fun arrayOfTheC(m: Int, t: Int, p: Int): Map<Pair<Int, Int>, BigInteger> =
    buildMap((p + 1) * t) {
      for (j in 1..p) {
        this[1 to j] = j.toBigInteger() * (j.toBigInteger() + ONE) / TWO
      }
      for (i in 2..t) {
        for (j in 0..p) {
          this[i to j] =
              if (j < i ceilDiv (m - 1)) {
                ZERO
              } else {
                this.getValue(i to (j - 1)) + j.toBigInteger() * this.getValue((i - 1) to j)
              }
        }
      }
    }

private sealed interface REOKResult

private data class Value(val value: List<Int>) : REOKResult

private data object ReduceP : REOKResult

private data object ReduceTAndAppendP : REOKResult

/** Implementation of the randomElementOfK function in Fig. 5 */
private fun randomElementOfKInternal(
    m: Int,
    t: Int,
    p: Int,
    random: Random,
    arrayT: Map<Pair<Int, Int>, BigInteger>
): REOKResult {
  if (p < t ceilDiv (m - 1)) {
    return Value(emptyList())
  }

  if (t == 1) {
    val dei = random.nextBigInteger(ONE, arrayT.getValue(1 to p))
    var de = dei
    var x = ONE
    while (de > x) {
      de -= x
      x += ONE
    }
    return Value(listOf(x.toInt()))
  } else {
    val de = random.nextBigInteger(ONE, arrayT.getValue(t to p))
    return if (de <= arrayT.getValue(t to (p - 1)) && p > 1) {
      ReduceP
    } else {
      ReduceTAndAppendP
    }
  }
}

internal fun randomElementOfK(m: Int, t: Int, p: Int, random: Random): List<Int> {
  val arrayT = arrayOfTheC(m, t, p)
  var currentT = t
  var currentP = p
  var result = randomElementOfKInternal(m, currentT, currentP, random, arrayT)
  val suffix = mutableListOf<Int>()
  while (true) {
    result =
        when (result) {
          is ReduceP -> {
            currentP--
            randomElementOfKInternal(m, currentT, currentP, random, arrayT)
          }
          is ReduceTAndAppendP -> {
            suffix.add(0, currentP)
            currentT--
            randomElementOfKInternal(m, currentT, currentP, random, arrayT)
          }
          is Value -> return result.value + suffix
        }
  }
}

/** Generate trees from a generalized tuple, see the proof of Proposition 4. */
internal fun <Alphabet> phiInverse(
    ks: List<Int>,
    alphabet: List<Alphabet>
): ExtendedTreeNode<Alphabet> {
  val (tree, remainder) = generalizedTupleToTree(emptyList(), listOf(1) + ks, alphabet)
  assert(remainder.isEmpty()) { "$remainder remaining" }
  return tree as ExtendedTreeNode<Alphabet>
}

private fun <Alphabet> generalizedTupleToTree(
    prefix: List<Alphabet>,
    ks: List<Int>,
    alphabet: List<Alphabet>
): Pair<ExtendedTree<Alphabet>, List<Int>> {
  val rootNode = ExtendedTreeNode(prefix)
  var currentKs = ks
  alphabet.forEach { suffix ->
    if (currentKs.size == 1) {
      // add the last missing leaf node
      assert(suffix == alphabet.last() && prefix.all { it == suffix })
      rootNode.children[suffix] = ExtendedTreeLeaf(prefix + suffix)
      currentKs = emptyList()
    } else {
      val firstK = currentKs.first()
      val secondK = currentKs[1]

      if (firstK == secondK) {
        // leaf: remove k_0 and add the leaf
        rootNode.children[suffix] = ExtendedTreeLeaf(prefix + suffix)
        currentKs = currentKs.subList(1, currentKs.size)
      } else {
        // node: increment k_0, recurse, and copy the ks after recursion completes
        val (subtree, newKs) =
            generalizedTupleToTree(
                prefix + suffix,
                listOf(firstK + 1) + currentKs.subList(1, currentKs.size),
                alphabet)
        rootNode.children[suffix] = subtree
        currentKs = newKs
      }
    }
  }
  return rootNode to currentKs
}

/** Transform a tree intro a DFA, see proof of Theorem 6., and perform RA transformation. */
internal fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> theorem6(
    tree: ExtendedTreeNode<Label>,
    alphabet: List<Label>,
    nParameters: Int,
    defaultGuard: GuardTheory,
    random: Random,
    acceptanceProbability: Double = 0.5,
    locationPrefix: String,
): RegisterAutomaton<Label, Alphabet, GuardTheory> {
  val nodes = tree.nodes
  val edges = tree.edges

  val alphabetToSymbol =
      alphabet.associateWith { LabeledSymbol(it, List(nParameters) { p -> Parameter("p$p") }) }

  val ra =
      DefaultRegisterAutomaton<Label, Alphabet, GuardTheory>(
          locationPrefix, random.nextFloat() < acceptanceProbability)

  val nodeToLocation =
      nodes.associateWith {
        if (it.accessSequence.isEmpty()) {
          ra.initialLocation
        } else {
          ra.addLocation(
              "$locationPrefix${it.accessSequence.joinToString("")}",
              random.nextFloat() < acceptanceProbability)
        }
      }
  edges
      .filter { it.to is ExtendedTreeNode<*> }
      .forEach { (from, symbol, to) ->
        ra.addTransition(
            from = nodeToLocation.getValue(from),
            symbol = alphabetToSymbol.getValue(symbol),
            guard = defaultGuard,
            assignment = emptyMap(),
            to = nodeToLocation.getValue(to as ExtendedTreeNode<Label>))
      }

  val alphabetIndex = alphabet.withIndex().associate { (index, symbol) -> symbol to index }
  operator fun List<Label>.compareTo(other: List<Label>): Int {
    (this zip other).forEach { (left, right) ->
      val comp = alphabetIndex.getValue(left).compareTo(alphabetIndex.getValue(right))
      if (comp != 0) return comp
    }
    return size - other.size
  }

  edges
      .filter { it.to is ExtendedTreeLeaf<*> }
      .forEach { (parent, symbol, leaf) ->
        val candidates = nodes.filter { it.accessSequence < leaf.accessSequence }
        ra.addTransition(
            from = nodeToLocation.getValue(parent),
            symbol = alphabetToSymbol.getValue(symbol),
            guard = defaultGuard,
            assignment = emptyMap(),
            to = nodeToLocation.getValue(candidates.random(random)))
      }

  return ra
}

/**
 * Generate a random DFA using the Champarnaud-Paranthoën algorithm nad transform it into an RA.
 *
 * @param Label the DFA's input alphabet.
 * @param Alphabet the data alphabet to generate.
 * @param GuardTheory the guard language to use.
 * @param nStates the number of locations in the resulting RA.
 * @param alphabet the label alphabet to use.
 * @param nParameters the arity each label should have in the final RA.
 * @param defaultGuard the guard to attach to every transition.
 * @param random the randomness source to use.
 * @param acceptanceProbability the probability for each generated location that it is accepting.
 * @param locationPrefix a prefix for each location name to avoid conflicts.
 * @return a register automaton matching the parameters.
 *
 * The algorithm used is described in
 * [Champarnaud, J.-M., &amp; Paranthoën, T. (2005). Random generation of DFAs. Theoretical Computer Science, 330(2), 221–235.](https://doi.org/10.1016/j.tcs.2004.03.072).
 */
fun <Label, Alphabet, GuardTheory : Formula<Alphabet>> champarnaudParanthoenRA(
    nStates: Int,
    alphabet: List<Label>,
    nParameters: Int,
    defaultGuard: GuardTheory,
    random: Random,
    acceptanceProbability: Double = 0.5,
    locationPrefix: String = "",
): RegisterAutomaton<Label, Alphabet, GuardTheory> {
  // see 4.1 for computation of t and p
  val tuple = randomElementOfK(alphabet.size, nStates * (alphabet.size - 1), nStates, random)
  val tree = phiInverse(tuple, alphabet)
  return theorem6(
      tree, alphabet, nParameters, defaultGuard, random, acceptanceProbability, locationPrefix)
}
