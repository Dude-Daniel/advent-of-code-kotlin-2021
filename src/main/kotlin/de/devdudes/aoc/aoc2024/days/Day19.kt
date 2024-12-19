package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day19 : Day(
    description = 19 - "Linen Layout",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of possible Designs",
            input = "day19",
            testInput = "day19_test",
            expectedTestResult = 6,
            solutionResult = 340,
            solution = { input ->
                TowelArrangementSolver(parseTowelConfig(input)).countValidDesigns()
            }
        )

        puzzle(
            description = 2 - "Total Number of possible Arrangements",
            input = "day19",
            testInput = "day19_test",
            expectedTestResult = 16L,
            solutionResult = 717_561_822_679_428L,
            solution = { input ->
                TowelArrangementSolver(parseTowelConfig(input)).countTotalNumberOfPossibleDesigns()
            }
        )
    }
)

private fun parseTowelConfig(input: List<String>): TowelConfig =
    TowelConfig(
        patterns = input.first().split(", "),
        designs = input.drop(2),
    )

private data class TowelConfig(
    val patterns: List<String>,
    val designs: List<String>,
)

private class TowelArrangementSolver(private val config: TowelConfig) {

    private val cachedCounts = mutableMapOf<String, Long>()

    fun countValidDesigns(): Int = config.designs.count { countPossibleBuildDesigns(it) > 0L }

    fun countTotalNumberOfPossibleDesigns(): Long = config.designs.sumOf { countPossibleBuildDesigns(it) }

    private fun countPossibleBuildDesigns(design: String): Long {
        if (design.isEmpty()) return 1L
        val cachedCount = cachedCounts[design]
        if (cachedCount != null) return cachedCount
        val count = config.patterns.sumOf { pattern ->
            if (design.startsWith(pattern)) {
                countPossibleBuildDesigns(design.drop(pattern.length))
            } else 0L
        }
        cachedCounts[design] = count
        return count
    }
}
