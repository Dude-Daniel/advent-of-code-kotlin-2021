package de.devdudes.aoc.aoc2021.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.math.BigInteger

class Day06 : Day(description = 6 - "Lanternfish", {

    puzzle(
        description = 1 - "Population after 80 days",
        input = "day06",
        testInput = "day06_test",
        expectedTestResult = 5934.toBigInteger(),
        solutionResult = 352151.toBigInteger(),
        solution = { input ->
            val initialPopulation = input.first().split(",").map { it.toInt() }
            initialPopulation.calculatePopulationAfter(days = 80)
        }
    )

    puzzle(
        description = 2 - "Population after 256 days",
        input = "day06",
        testInput = "day06_test",
        expectedTestResult = 26984457539.toBigInteger(),
        solutionResult = 1601616884019.toBigInteger(),
        solution = { input ->
            val initialPopulation = input.first().split(",").map { it.toInt() }
            initialPopulation.calculatePopulationAfter(days = 256)
        }
    )
})

private fun MutableMap<Int, BigInteger>.addOrSet(key: Int, value: BigInteger) {
    val currentValue = get(key)
    val newValue = if (currentValue == null) value else value + currentValue
    put(key, newValue)
}

private fun List<Int>.calculatePopulationAfter(days: Int): BigInteger {
    val populationsByAge = this.groupBy { it }.mapValues { it.value.size.toBigInteger() }
    var populations = populationsByAge
    for (i in 1..days) {
        populations = mutableMapOf<Int, BigInteger>().apply {
            populations.keys.forEach { key ->
                val currentPopulationCount = populations[key]!!
                if (key == 0) {
                    addOrSet(6, currentPopulationCount)
                    addOrSet(8, currentPopulationCount)
                } else {
                    addOrSet(key - 1, currentPopulationCount)
                }
            }
        }
    }

    return populations.values.fold(BigInteger.ZERO) { acc, current -> acc + current }
}
