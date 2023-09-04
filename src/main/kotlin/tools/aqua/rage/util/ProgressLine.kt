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

package tools.aqua.rage.tools.aqua.rage.util

import java.nio.charset.Charset.defaultCharset
import kotlin.time.Duration
import kotlin.time.TimeSource
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp.Capability.column_address

/**
 * Tracks task execution progress and outputs a TUI progress line with estimated time remaining. If
 * the terminal supports it, the line will be rewritten dynamically. This should not be combined
 * with other command line output!
 *
 * @property totalTasks the number of tasks that will be executed in total.
 */
class ProgressLine(private val totalTasks: Int) : AutoCloseable {
  private val terminal = TerminalBuilder.builder().system(true).build()
  private val startTime = TimeSource.Monotonic.markNow()
  private var tasksDone = 0

  private val nTasksStr = totalTasks.toString()

  /** Report the completion of [nTasks] tasks. Causes an update of the status line. */
  fun reportDone(nTasks: Int) {
    require(nTasks >= 0 && tasksDone + nTasks <= totalTasks)
    if (nTasks == 0) return

    tasksDone += nTasks
    printStatusLine()
  }

  /** True iff all tasks have been reported complete. */
  val done: Boolean
    get() = tasksDone == totalTasks

  private fun printStatusLine() {
    val fractionDone = tasksDone.toDouble() / totalTasks
    val timeElapsed = startTime.elapsedNow()

    val prefix = "(${tasksDone.toString().padStart(nTasksStr.length)}/$nTasksStr) ["
    val suffix =
        "] ${
                (fractionDone * 100).toInt().toString().padStart(3)
            }% (${formatTime(timeElapsed)}/${formatTime(timeElapsed / fractionDone)})"

    val freeSpace = terminal.width.let { if (it <= 0) 80 else it } - prefix.length - suffix.length
    val progressBar =
        if (freeSpace > 0) {
          val progress = (freeSpace * fractionDone).toInt()
          "=".repeat(progress) + " ".repeat(freeSpace - progress)
        } else ""

    if (!terminal.puts(column_address, 0)) {
      terminal.output().write("\r".toByteArray(defaultCharset()))
    }
    terminal.output().write("$prefix$progressBar$suffix".toByteArray(defaultCharset()))
    terminal.flush()
  }

  private fun formatTime(time: Duration): String =
      time.toComponents { hours, minutes, seconds, _ ->
        (if (hours > 0) "$hours:" else "") +
            "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
      }

  /** Release the terminal output. */
  override fun close() {
    terminal.output().write("\n".toByteArray(defaultCharset()))
    terminal.close()
  }
}
