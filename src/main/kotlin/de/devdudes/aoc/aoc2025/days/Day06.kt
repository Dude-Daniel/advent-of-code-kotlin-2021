package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitAtMatchingPositions
import de.devdudes.aoc.helpers.transpose

class Day06 : Day(
    description = 6 - "Trash Compactor",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Solve Math Problems",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 4_277_556L,
            solutionResult = 4_580_995_422_905L,
            solution = { input ->
                input.splitAtMatchingPositions { it.toString().isBlank() }
                    .transpose()
                    .map { data ->
                        MathProblem(
                            numbers = data.dropLast(1).map { it.trim().toInt() },
                            operation = MathOperation.from(data.last().trim()),
                        )
                    }.sumOf(MathProblem::solve)
            }
        )

        puzzle(
            description = 2 - "Solve transposed Math Problems",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 3_263_827L,
            solutionResult = 10_875_057_285_868L,
            solution = { input ->
                input.splitAtMatchingPositions { it.toString().isBlank() }
                    .transpose()
                    .map { data ->
                        val transposedNumbers = data.dropLast(1)
                            .map { it.toCharArray().asList() }
                            .transpose()
                            .map { it.joinToString(separator = "") }

                        MathProblem(
                            numbers = transposedNumbers.map { it.trim().toInt() },
                            operation = MathOperation.from(data.last().trim()),
                        )
                    }.sumOf(MathProblem::solve)
            }
        )
    }
)

private data class MathProblem(
    val numbers: List<Int>,
    val operation: MathOperation,
) {
    fun solve(): Long =
        when (operation) {
            MathOperation.Add -> numbers.sumOf(Int::toLong)
            MathOperation.Multiply -> numbers.fold(1L) { acc, number -> acc * number }
        }
}

private sealed class MathOperation {
    data object Add : MathOperation()
    data object Multiply : MathOperation()

    companion object {
        fun from(operation: String): MathOperation =
            when (operation) {
                "+" -> Add
                "*" -> Multiply
                else -> error("unknown operation: $operation")
            }
    }
}
