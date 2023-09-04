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

import guru.zoroark.tegral.niwen.lexer.TokenType
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.niwenLexer
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.dsl.either
import guru.zoroark.tegral.niwen.parser.dsl.item
import guru.zoroark.tegral.niwen.parser.dsl.niwenParser
import guru.zoroark.tegral.niwen.parser.dsl.or
import guru.zoroark.tegral.niwen.parser.dsl.repeated
import guru.zoroark.tegral.niwen.parser.dsl.self
import guru.zoroark.tegral.niwen.parser.dsl.subtype
import guru.zoroark.tegral.niwen.parser.reflective
import tools.aqua.rage.wiki.GuardTokens.AND
import tools.aqua.rage.wiki.GuardTokens.EQUALS
import tools.aqua.rage.wiki.GuardTokens.GREATER
import tools.aqua.rage.wiki.GuardTokens.GREATER_EQUALS
import tools.aqua.rage.wiki.GuardTokens.IDENTIFIER
import tools.aqua.rage.wiki.GuardTokens.LEFT_PARANTHESIS
import tools.aqua.rage.wiki.GuardTokens.LESS
import tools.aqua.rage.wiki.GuardTokens.LESS_EQUALS
import tools.aqua.rage.wiki.GuardTokens.NOT_EQUALS
import tools.aqua.rage.wiki.GuardTokens.NUMERAL
import tools.aqua.rage.wiki.GuardTokens.OR
import tools.aqua.rage.wiki.GuardTokens.RIGHT_PARANTHESIS

private enum class GuardTokens : TokenType {
  LEFT_PARANTHESIS,
  RIGHT_PARANTHESIS,
  IDENTIFIER,
  NUMERAL,
  AND,
  OR,
  EQUALS,
  NOT_EQUALS,
  GREATER_EQUALS,
  GREATER,
  LESS_EQUALS,
  LESS
}

private val guardLexer = niwenLexer {
  state {
    matches("""\s+""").ignore
    '(' isToken LEFT_PARANTHESIS
    ')' isToken RIGHT_PARANTHESIS
    matches("""[a-zA-Z]\w*""") isToken IDENTIFIER
    matches("""-?\d+""") isToken NUMERAL

    "&&" isToken AND
    "||" isToken OR

    "==" isToken EQUALS
    "!=" isToken NOT_EQUALS
    ">=" isToken GREATER_EQUALS
    '>' isToken GREATER
    "<=" isToken LESS_EQUALS
    '<' isToken LESS
  }
}

internal data class AndChainNode(val head: OrChainNode, val tail: List<OrChainNode>) {
  val elements: List<OrChainNode> = tail.toMutableList().also { it.add(0, head) }

  companion object : ParserNodeDeclaration<AndChainNode> by reflective()
}

internal data class OrChainNode(val head: ClauseNode, val tail: List<ClauseNode>) {
  val elements: List<ClauseNode> = tail.toMutableList().also { it.add(0, head) }

  companion object : ParserNodeDeclaration<OrChainNode> by reflective()
}

internal sealed interface ClauseNode {
  companion object : ParserNodeDeclaration<ClauseNode> by subtype()
}

internal data class EqualNode(val left: ExpressionNode, val right: ExpressionNode) : ClauseNode {
  companion object : ParserNodeDeclaration<EqualNode> by reflective()
}

internal data class NotEqualNode(val left: ExpressionNode, val right: ExpressionNode) : ClauseNode {
  companion object : ParserNodeDeclaration<NotEqualNode> by reflective()
}

internal data class GreaterEqualNode(val left: ExpressionNode, val right: ExpressionNode) :
    ClauseNode {
  companion object : ParserNodeDeclaration<GreaterEqualNode> by reflective()
}

internal data class GreaterNode(val left: ExpressionNode, val right: ExpressionNode) : ClauseNode {
  companion object : ParserNodeDeclaration<GreaterNode> by reflective()
}

internal data class LessEqualNode(val left: ExpressionNode, val right: ExpressionNode) :
    ClauseNode {
  companion object : ParserNodeDeclaration<LessEqualNode> by reflective()
}

internal data class LessNode(val left: ExpressionNode, val right: ExpressionNode) : ClauseNode {
  companion object : ParserNodeDeclaration<LessNode> by reflective()
}

internal data class BracedGuardNode(val guard: AndChainNode) : ClauseNode {
  companion object : ParserNodeDeclaration<BracedGuardNode> by reflective()
}

internal sealed interface ExpressionNode {
  companion object : ParserNodeDeclaration<ExpressionNode> by subtype()
}

internal data class LiteralNode(val value: Int) : ExpressionNode {
  companion object : ParserNodeDeclaration<LiteralNode> by reflective()
}

internal data class VariableNode(val identifier: String) : ExpressionNode {
  companion object : ParserNodeDeclaration<VariableNode> by reflective()
}

private val guardParser = niwenParser {
  AndChainNode root
      {
        +OrChainNode storeIn AndChainNode::head
        @Suppress("RemoveExplicitTypeArguments") // compiler type inference fails here
        repeated<AndChainNode, OrChainNode> {
          +AND
          +OrChainNode storeIn item
        } storeIn AndChainNode::tail
      }

  OrChainNode {
    +ClauseNode storeIn OrChainNode::head
    repeated {
      +OR
      +ClauseNode storeIn item
    } storeIn OrChainNode::tail
  }

  ClauseNode {
    either { +EqualNode storeIn self() } or
        {
          +NotEqualNode storeIn self()
        } or
        {
          +GreaterEqualNode storeIn self()
        } or
        {
          +GreaterNode storeIn self()
        } or
        {
          +LessEqualNode storeIn self()
        } or
        {
          +LessNode storeIn self()
        } or
        {
          +BracedGuardNode storeIn self()
        }
  }

  EqualNode {
    +ExpressionNode storeIn EqualNode::left
    +EQUALS
    +ExpressionNode storeIn EqualNode::right
  }

  NotEqualNode {
    +ExpressionNode storeIn NotEqualNode::left
    +NOT_EQUALS
    +ExpressionNode storeIn NotEqualNode::right
  }

  GreaterEqualNode {
    +ExpressionNode storeIn GreaterEqualNode::left
    +GREATER_EQUALS
    +ExpressionNode storeIn GreaterEqualNode::right
  }

  GreaterNode {
    +ExpressionNode storeIn GreaterNode::left
    +GREATER
    +ExpressionNode storeIn GreaterNode::right
  }

  LessEqualNode {
    +ExpressionNode storeIn LessEqualNode::left
    +LESS_EQUALS
    +ExpressionNode storeIn LessEqualNode::right
  }

  LessNode {
    +ExpressionNode storeIn LessNode::left
    +LESS
    +ExpressionNode storeIn LessNode::right
  }

  BracedGuardNode {
    +LEFT_PARANTHESIS
    +AndChainNode storeIn BracedGuardNode::guard
    +RIGHT_PARANTHESIS
  }

  ExpressionNode { either { +VariableNode storeIn self() } or { +LiteralNode storeIn self() } }

  VariableNode { +IDENTIFIER storeIn VariableNode::identifier }

  LiteralNode { +NUMERAL transform String::toInt storeIn LiteralNode::value }
}

/** Parse this string as a guard in the Automata Wiki expression language. */
fun String.parseGuard(): Guard =
    if (isBlank()) {
      True
    } else {
      guardParser.parse(guardLexer.tokenize(this)).toGuard()
    }

private fun AndChainNode.toGuard(): Guard =
    if (tail.isEmpty()) {
      head.toGuard()
    } else {
      ComplexAnd(elements.map { it.toGuard() })
    }

private fun OrChainNode.toGuard(): Guard =
    if (tail.isEmpty()) {
      head.toGuard()
    } else {
      ComplexOr(elements.map { it.toGuard() })
    }

private fun ClauseNode.toGuard(): Guard =
    when (this) {
      is EqualNode -> Equal(left.toExpression(), right.toExpression())
      is NotEqualNode -> NotEqual(left.toExpression(), right.toExpression())
      is GreaterEqualNode -> GreaterEqual(left.toExpression(), right.toExpression())
      is GreaterNode -> Greater(left.toExpression(), right.toExpression())
      is LessEqualNode -> LessEqual(left.toExpression(), right.toExpression())
      is LessNode -> Less(left.toExpression(), right.toExpression())
      is BracedGuardNode -> guard.toGuard()
    }

private val expressionParser = niwenParser {
  ExpressionNode root { either { +VariableNode storeIn self() } or { +LiteralNode storeIn self() } }

  VariableNode { +IDENTIFIER storeIn VariableNode::identifier }

  LiteralNode { +NUMERAL transform String::toInt storeIn LiteralNode::value }
}

/** Parse this string as an expression in the Automata Wiki expression language. */
fun String.parseExpression(): Expression =
    expressionParser.parse(guardLexer.tokenize(this)).toExpression()

private fun ExpressionNode.toExpression(): Expression =
    when (this) {
      is VariableNode -> Variable(identifier)
      is LiteralNode -> Constant(value)
    }
