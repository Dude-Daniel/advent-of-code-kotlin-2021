package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import kotlin.math.abs

class Day02 : Day(
    description = 2 - "Red-Nosed Reports",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Safe Reports",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 2,
            solutionResult = 326,
            solution = { input ->
                RedNosedReport(input).calculateValidReports()
            }
        )

        puzzle(
            description = 2 - "Safe Reports with max one wrong Level",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 4,
            solutionResult = 381,
            solution = { input ->
                RedNosedReport(input).calculateValidReportsWithOneError()
            }
        )
    }
)


private class RedNosedReport(private val input: List<String>) {

    private fun parseLevels(): List<List<Int>> =
        input.map { line ->
            line.split(" ")
                .map(String::toInt)
        }

    fun calculateValidReports(): Int = parseLevels().count { level -> isLevelValid(level) }

    fun calculateValidReportsWithOneError(): Int =
        parseLevels().count { report ->
            if (isLevelValid(report)) {
                // report is valid - no error correction needed
                true
            } else {
                // check report by removing a single level
                report.indices.any { index ->
                    val subReport = report.toMutableList().apply { removeAt(index) }
                    isLevelValid(subReport)
                }
            }
        }

    private fun isLevelValid(level: List<Int>): Boolean {
        val distinctLevel = level.distinct()
        val levelsSortedAsc = level.sorted()
        val levelsSortedDesc = level.sortedDescending()
        val levelsDifferAtMostByThree = level.windowed(2, 1).all { (first, second) -> abs(second - first) <= 3 }

        return when {
            distinctLevel.size != level.size -> {
                // report contains duplicate levels
                false
            }

            levelsSortedAsc == level || levelsSortedDesc == level -> {
                levelsDifferAtMostByThree
            }

            else -> {
                // levels are not sorted in any direction
                false
            }
        }
    }
}
