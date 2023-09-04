<!--
   SPDX-License-Identifier: CC-BY-4.0

   Copyright 2023-2023 The RAGe Authors

   This work is licensed under the Creative Commons Attribution 4.0
   International License.

   You should have received a copy of the license along with this
   work. If not, see <https://creativecommons.org/licenses/by/4.0/>.
-->

# RAGe: A Generator for Register Automata

RAGe is a tool for semi-random generation of register automata [^1]. It provides two strategies for
generation:

1. Random generation of a DFA using the Champarnaud-Paranthoën algorithm [^2] and subsequent
   transformation into an RA
2. Manual definition using a Kotlin DSL.

Additionally, three combination mechanisms for automata are included:

1. Concatenation
2. Random replacement of transitions with sub-RAs
3. Splitting one location and introducing an RA to obscure the discriminating suffixes

### Using

Pre-build installers and distributions (using `jpackage`) are available and expose a command-line
interface to some preconfigured generators. Alternatively, RAGe can be used as a library via Maven
and custom generation campaigns can be configured.

[^1]:
    [Cassel, S., Howar, F., Jönsson, B., Merten, M. & Steffen, B. (2015). A succinct canonical Register Automaton model. Journal of Logical and Algebraic Methods in Programming, 84(1), 54–66.](https://doi.org/10.1016/j.jlamp.2014.07.004)

[^2]:
    [Champarnaud, J.-M., &amp; Paranthoën, T. (2005). Random generation of DFAs. Theoretical Computer Science, 330(2), 221–235.](https://doi.org/10.1016/j.tcs.2004.03.072).
