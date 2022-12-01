package de.devdudes.aoc.aoc2021.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import kotlin.math.abs

class Day07 : Day(description = 7 - "The Treachery of Whales", {

    puzzle(
        description = 1 - "1 fuel per step",
        input = "day07",
        testInput = "day07_test",
        expectedTestResult = 37,
        solutionResult = 357353,
        solution = { input ->
            val crabPositions = input.first().split(",").map { it.toInt() }
            val fuelCosts = crabPositions.calculateAllFuelCosts { stepCount ->
                // one fuel per step
                stepCount
            }
            fuelCosts.minOf { it }
        }
    )

    puzzle(
        description = 2 - "incremental fuel per step",
        input = "day07",
        testInput = "day07_test",
        expectedTestResult = 168,
        solutionResult = 104822130,
        solution = { input ->
            val crabPositions = input.first().split(",").map { it.toInt() }
            val fuelCosts = crabPositions.calculateAllFuelCosts { stepCount ->
                // one fuel for 1st step, 2 for 2nd step, etc.
                (1..stepCount).sum()
            }
            fuelCosts.minOf { it }
        }
    )
})

private fun List<Int>.calculateAllFuelCosts(calculator: (stepCount: Int) -> Int): List<Int> {
    val maxPosition = maxOf { it }
    return (1..maxPosition).map { position -> calculateFuelCost(position, calculator) }
}

private fun List<Int>.calculateFuelCost(position: Int, calculator: (steps: Int) -> Int): Int =
    sumOf { calculator(abs(it - position)) }
