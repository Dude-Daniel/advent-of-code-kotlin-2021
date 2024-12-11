package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.isOdd
import de.devdudes.aoc.helpers.length
import kotlin.math.pow

class Day11 : Day(
    description = 11 - "Plutonian Pebbles - Number of Stones after Blinks",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "25 Blinks",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 55_312L,
            solutionResult = 204_022L,
            solution = { input ->
                parseStoneRow(input).blinkFor(25)
            }
        )

        puzzle(
            description = 2 - "75 Blinks",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 65_601_038_650_482L,
            solutionResult = 241_651_071_960_597L,
            solution = { input ->
                parseStoneRow(input).blinkFor(75)
            }
        )
    }
)

private fun parseStoneRow(input: List<String>): StoneRow =
    input.first()
        .split(" ")
        .map { it.toLong() }
        .let(::StoneRow)

private data class StoneRow(val stones: List<Long>) {

    private var cachedStoneCounts = mutableMapOf<Pair<Long, Int>, Long>()

    fun blinkFor(times: Int): Long =
        stones.sumOf { stone ->
            calculateResultingStoneCount(stone, times)
        }

    private fun calculateResultingStoneCount(stone: Long, steps: Int): Long {
        val cacheKey = stone to steps
        val cachedValue = cachedStoneCounts[cacheKey]
        if (cachedValue != null) return cachedValue

        val remainingSteps = steps - 1
        val digitCount = stone.length()
        return when {
            steps == 0 -> 1
            stone == 0L -> calculateResultingStoneCount(1, remainingSteps)
            digitCount.isOdd() -> calculateResultingStoneCount(stone * 2024, remainingSteps)
            else -> {
                val digitCountHalf = digitCount / 2
                val mask = 10.0.pow(digitCountHalf).toInt()
                val numberA = stone / mask
                val numberB = stone % mask
                calculateResultingStoneCount(numberA, remainingSteps) + calculateResultingStoneCount(numberB, remainingSteps)
            }
        }.also { valueToCache -> cachedStoneCounts[cacheKey] = valueToCache }
    }
}
