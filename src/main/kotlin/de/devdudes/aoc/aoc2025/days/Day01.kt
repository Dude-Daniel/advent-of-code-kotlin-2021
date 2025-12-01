package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import kotlin.math.absoluteValue
import kotlin.math.sign

class Day01 : Day(
    description = 1 - "Secret Entrance",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Count of Zero Position after each Rotation",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 3,
            solutionResult = 1141,
            solution = { input ->
                SafeSolver(input).countZeroPositionsAfterEachRotation()
            }
        )

        puzzle(
            description = 2 - "Count of Zero Position after each Click",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 6,
            solutionResult = 6634,
            solution = { input ->
                SafeSolver(input).countZeroPositionsAfterEachClick()
            }
        )
    }
)

private class SafeSolver(input: List<String>) {

    private val rotations: List<SafeDialRotation> = input.map { rawRotation ->
        val steps = rawRotation.drop(1).toInt()
        when (val char = rawRotation.first()) {
            'L' -> SafeDialRotation(-steps)
            'R' -> SafeDialRotation(steps)
            else -> error("Unsupported direction $char")
        }
    }

    fun countZeroPositionsAfterEachRotation(): Int {
        var pointedAtZero = 0
        rotations.fold(initial = 50) { acc, rotation ->
            val result = (acc + rotation.steps).mod(DIAL_SIZE)
            if (result == 0) {
                pointedAtZero++
            }
            result
        }
        return pointedAtZero
    }

    fun countZeroPositionsAfterEachClick(): Int {
        var pointedAtZero = 0

        rotations.fold(initial = 50) { acc, rotation ->
            val result = acc + rotation.steps
            when (result.sign) {
                -1 -> {
                    // subtraction
                    if (acc > 0) pointedAtZero += 1 // add 1 when dial reached 0 position while moving in negative direction
                    pointedAtZero += (result / DIAL_SIZE).absoluteValue
                }

                0 -> pointedAtZero += 1 // subtraction ended at exactly 0
                1 -> {
                    // either subtraction or addition
                    pointedAtZero += (result / DIAL_SIZE)
                }
            }
            result.mod(DIAL_SIZE)
        }

        return pointedAtZero
    }

    companion object {
        private const val DIAL_SIZE = 100
    }
}

private data class SafeDialRotation(val steps: Int)
