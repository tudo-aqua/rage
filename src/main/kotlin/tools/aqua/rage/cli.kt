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

package tools.aqua.rage

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import tools.aqua.rage.commands.DfaRaDfa
import tools.aqua.rage.commands.DfaReplaceWithRa
import tools.aqua.rage.commands.DfaSingleDiscriminator

private object RAGe : CliktCommand(name = "rage") {
  override fun run() = Unit
}

/** Program main entry point, accepts the CLI [args]. */
fun main(args: Array<String>) =
    RAGe.subcommands(DfaRaDfa, DfaReplaceWithRa, DfaSingleDiscriminator).main(args)
