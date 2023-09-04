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

import com.github.ajalt.clikt.core.CliktCommand
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import tools.aqua.rage.tools.aqua.rage.util.ProgressLine

/**
 * Wrapper class for commands that perform RA generation. This class collects generation subtasks,
 * executes them, and reports the generation progress on the command line. Parameters are passed
 * as-is to [CliktCommand].
 */
abstract class GenerationCommand(
    help: String = "",
    epilog: String = "",
    name: String? = null,
    invokeWithoutSubcommand: Boolean = false,
    printHelpOnEmptyArgs: Boolean = false,
    helpTags: Map<String, String> = emptyMap(),
    autoCompleteEnvvar: String? = "",
    allowMultipleSubcommands: Boolean = false,
    treatUnknownOptionsAsArgs: Boolean = false,
    hidden: Boolean = false,
) :
    CliktCommand(
        help,
        epilog,
        name,
        invokeWithoutSubcommand,
        printHelpOnEmptyArgs,
        helpTags,
        autoCompleteEnvvar,
        allowMultipleSubcommands,
        treatUnknownOptionsAsArgs,
        hidden) {

  /** Generate the list of tasks to be executed. The tasks will not be executed in-order. */
  protected abstract fun createTasks(): List<Callable<Path>>

  override fun run() {
    val tasks = createTasks().shuffled()

    val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    val executor = ExecutorCompletionService<Path>(pool)

    ProgressLine(tasks.size).use { progress ->
      tasks.map(executor::submit)
      while (!progress.done) {
        executor.take().get()
        var done = 1
        while (executor.poll()?.get() != null) {
          done++ // fast-forward
        }
        progress.reportDone(done)
      }
    }

    pool.shutdown()
  }
}
