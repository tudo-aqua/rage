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

internal sealed interface ExtendedTree<Alphabet> {
  val accessSequence: List<Alphabet>
}

internal data class ExtendedTreeNode<Alphabet>(
    override val accessSequence: List<Alphabet>,
    val children: MutableMap<Alphabet, ExtendedTree<Alphabet>> = mutableMapOf()
) : ExtendedTree<Alphabet>

internal data class ExtendedTreeLeaf<Alphabet>(
    override val accessSequence: List<Alphabet>,
) : ExtendedTree<Alphabet>

internal val <Alphabet> ExtendedTree<Alphabet>.nodes: Set<ExtendedTreeNode<Alphabet>>
  get() =
      when (this) {
        is ExtendedTreeNode<Alphabet> ->
            children.values.flatMapTo(mutableSetOf()) { it.nodes } + this
        is ExtendedTreeLeaf<Alphabet> -> emptySet()
      }

internal data class ExtendedTreeEdge<Alphabet>(
    val from: ExtendedTreeNode<Alphabet>,
    val symbol: Alphabet,
    val to: ExtendedTree<Alphabet>,
)

internal val <Alphabet> ExtendedTree<Alphabet>.edges: Set<ExtendedTreeEdge<Alphabet>>
  get() =
      when (this) {
        is ExtendedTreeNode<Alphabet> ->
            children.flatMapTo(mutableSetOf()) { (symbol, child) ->
              child.edges + ExtendedTreeEdge(this, symbol, child)
            }
        is ExtendedTreeLeaf<Alphabet> -> emptySet()
      }

@DslMarker internal annotation class NodeDsl

@NodeDsl
internal class NodeBuilder<Alphabet>(private val prefix: List<Alphabet>) {
  private val node = ExtendedTreeNode(prefix, mutableMapOf())

  @NodeDsl
  fun node(suffix: Alphabet, action: NodeBuilder<Alphabet>.() -> Unit) {
    node.children[suffix] = NodeBuilder(prefix + suffix).also(action).build()
  }

  @NodeDsl
  fun leaf(suffix: Alphabet) {
    node.children[suffix] = ExtendedTreeLeaf(prefix + suffix)
  }

  fun build(): ExtendedTreeNode<Alphabet> = node
}

@NodeDsl
internal fun <Alphabet> tree(action: NodeBuilder<Alphabet>.() -> Unit): ExtendedTreeNode<Alphabet> =
    NodeBuilder<Alphabet>(listOf()).also(action).build()
