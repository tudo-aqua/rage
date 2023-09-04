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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Codec that encodes a list of strings as a CSV expression (i.e., `[e0, e1, e2]` becomes
 * `e0,e1,e2`).
 */
object CSVSerializer : KSerializer<List<String>> {
  override val descriptor: SerialDescriptor = String.serializer().descriptor

  override fun serialize(encoder: Encoder, value: List<String>) =
      encoder.encodeString(value.joinToString(","))

  override fun deserialize(decoder: Decoder): List<String> = decoder.decodeString().split(',')
}

/** Root class for (de-)serializing guards in the Automata Wiki syntax. */
abstract class GuardSerializer : KSerializer<Guard> {
  override val descriptor: SerialDescriptor = String.serializer().descriptor

  override fun deserialize(decoder: Decoder): Guard = decoder.decodeString().parseGuard()
}

/** Codec for guards in the Automata Wiki syntax. Guards are encoded as-is. */
object AutomataWikiGuardSerializer : GuardSerializer() {
  override fun serialize(encoder: Encoder, value: Guard) =
      encoder.encodeString(value.toAutomataWikiString())
}

/**
 * Codec for guards in the RALib-safe subset of the Automata Wiki syntax. Guards are transformed on
 * encoding to be compliant, the decoder accepts all guards.
 */
object RALibGuardSerializer : GuardSerializer() {
  override fun serialize(encoder: Encoder, value: Guard) =
      encoder.encodeString(
          value.simplifyInequalities().toDisjunctiveNormalForm().toRALibSafeAutomataWikiString())
}

/** Codec for expressions in the Automata Wiki syntax. */
object ExpressionSerializer : KSerializer<Expression> {
  override val descriptor: SerialDescriptor = String.serializer().descriptor

  override fun serialize(encoder: Encoder, value: Expression) =
      encoder.encodeString(value.toAutomataWikiString())

  override fun deserialize(decoder: Decoder): Expression = decoder.decodeString().parseExpression()
}
