package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day09 : Day(
    description = 9 - "Mirage Maintenance",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Prediction of next values",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 114,
            solutionResult = 1_702_218_515,
            solution = { input ->
                parseOasisData(input)
                    .predictNextValues()
                    .sum()
            }
        )

        puzzle(
            description = 2 - "Prediction of previous values",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 2,
            solutionResult = 925,
            solution = { input ->
                parseOasisData(input)
                    .predictPreviousValues()
                    .sum()
            }
        )
    }
)

private fun parseOasisData(input: List<String>): OasisData =
    input.map {
        // input per row row: '0 3 6 9 12 15'
        it.split(" ")
            .map(String::toInt)
            .let(::OasisSequence)
    }.let(::OasisData)

private data class OasisData(val sequences: List<OasisSequence>) {

    fun predictNextValues(): List<Int> = sequences.map { it.predictNextValue() }

    fun predictPreviousValues(): List<Int> = sequences.map { it.predictPreviousValue() }
}

private data class OasisSequence(val values: List<Int>) {

    fun predictNextValue(): Int {
        return calculateDifferences(differences = values, add = true) + values.last()
    }

    fun predictPreviousValue(): Int {
        return values.first() - calculateDifferences(differences = values.reversed(), add = false)
    }

    private fun calculateDifferences(differences: List<Int>, add: Boolean): Int {
        // when all values are the same (their difference is 0) the final differences are found
        return if (differences.groupBy { it }.keys.size == 1) {
            0
        } else {
            // calculate the delta of all values next to each other
            val newDifferences = differences.windowed(size = 2, step = 1)
                .map { (first, second) -> second - first }

            val difference = calculateDifferences(differences = newDifferences, add = add)

            // add or subtract values
            if (add) difference + newDifferences.last()
            else difference - newDifferences.last()
        }
    }

}
