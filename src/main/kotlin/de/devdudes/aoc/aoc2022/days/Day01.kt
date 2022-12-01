package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen

class Day01 : Day(
    description = 1 - "Unknown",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Unknown",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 24000L,
            solutionResult = 67450L,
            solution = { input ->
                input.splitWhen { it.isEmpty() }
                    .map { list -> list.map { it.toLong() } }
                    .map { it.sum() }
                    .maxOf { it }
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 45000L,
            solutionResult = 199357L,
            solution = { input ->
                input.splitWhen { it.isEmpty() }
                    .asSequence()
                    .map { list -> list.map { it.toLong() } }
                    .map { it.sum() }
                    .sortedDescending()
                    .take(3)
                    .sum()
            }
        )
    }
)
