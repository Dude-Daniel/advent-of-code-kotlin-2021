package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.concat

class Day07 : Day(
    description = 7 - "Bridge Repair - Solve Equations",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Add and Multiply",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 3_749L,
            solutionResult = 663_613_490_587L,
            solution = { input ->
                BridgeCalibration(input).sumResultsOfValidEquations(supportsConcatinationOperators = false)
            }
        )

        puzzle(
            description = 2 - "Add, Multiply and Concatenate",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 11_387L,
            solutionResult = 110_365_987_435_001L,
            solution = { input ->
                BridgeCalibration(input).sumResultsOfValidEquations(supportsConcatinationOperators = true)
            }
        )
    }
)

private data class CalibrationEquation(val result: Long, val numbers: List<Long>)

private class BridgeCalibration(private val input: List<String>) {

    private fun parseCalibrations(): List<CalibrationEquation> =
        input.map { line ->
            val (result, numbers) = line.split(": ")
            val numbersList = numbers.split(" ").map { it.toLong() }
            CalibrationEquation(result.toLong(), numbersList)
        }

    fun sumResultsOfValidEquations(supportsConcatinationOperators: Boolean): Long =
        parseCalibrations()
            .sumOf { equation ->
                val first = equation.numbers.first()
                val allButFirst = equation.numbers.drop(1)

                val equationResults = allButFirst.fold(listOf(first)) { acc, number ->
                    buildList {
                        acc.forEach { value ->
                            if (value + number <= equation.result) add(value + number)
                            if (value * number <= equation.result) add(value * number)

                            if (supportsConcatinationOperators) {
                                val accValue = value concat number
                                if (accValue <= equation.result) add(accValue)
                            }
                        }
                    }
                }
                if (equationResults.any { it == equation.result }) equation.result else 0
            }
}
