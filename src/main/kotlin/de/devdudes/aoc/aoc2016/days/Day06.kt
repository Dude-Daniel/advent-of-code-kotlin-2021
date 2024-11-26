package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.transpose

class Day06 : Day(
    description = 6 - "Signals and Noise",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Most Common Characters",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = "easter",
            solutionResult = "usccerug",
            solution = { input ->
                input.map { it.toCharArray().toList() }
                    .transpose()
                    .map { rowChars ->
                        rowChars.groupBy { it }
                            .maxBy { it.value.size }
                            .key
                    }
                    .joinToString("")
            }
        )

        puzzle(
            description = 2 - "Least Common Characters",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = "advent",
            solutionResult = "cnvvtafc",
            solution = { input ->
                input.map { it.toCharArray().toList() }
                    .transpose()
                    .map { rowChars ->
                        rowChars.groupBy { it }
                            .minBy { it.value.size }
                            .key
                    }
                    .joinToString("")
            }
        )
    }
)
