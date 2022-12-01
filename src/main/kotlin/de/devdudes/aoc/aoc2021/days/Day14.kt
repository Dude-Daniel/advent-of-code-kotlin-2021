package de.devdudes.aoc.aoc2021.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.math.BigInteger

class Day14 : Day(description = 14 - "Extended Polymerization", {

    puzzle(
        description = 1 - "10 steps",
        input = "day14",
        testInput = "day14_test",
        expectedTestResult = 1588.toBigInteger(),
        solutionResult = 3213.toBigInteger(),
        solution = { input ->
            PolymerizationGenerator(input).applySteps(10)
        }
    )

    puzzle(
        description = 2 - "40 steps",
        input = "day14",
        testInput = "day14_test",
        expectedTestResult = 2188189693529.toBigInteger(),
        solutionResult = 3711743744429.toBigInteger(),
        solution = { input ->
            PolymerizationGenerator(input).applySteps(40)
        }
    )
})

private class PolymerizationGenerator(input: List<String>) {

    private val template: String
    private val insertionRules: Map<String, String>

    init {
        template = input.first()
        insertionRules = input.drop(2)
            .map { line ->
                val (sequence, insert) = line.split(" -> ")
                Pair(sequence, insert)
            }.groupBy { it.first }
            .mapValues { entry -> entry.value.first().second }
    }

    fun applySteps(count: Int): BigInteger {
        var pairs = countPairs(template)
        repeat(count) {
            pairs = applyStep(pairs)
        }

        val charCounts = countOfChars(pairs).values.sorted()
        return charCounts.last() - charCounts.first()
    }

    private fun countPairs(template: String): Map<String, BigInteger> =
        template.windowed(size = 2)
            .groupBy { it }
            .mapValues { entry -> entry.value.size.toBigInteger() }

    private fun countOfChars(pairs: Map<String, BigInteger>): Map<Char, BigInteger> =
        pairs.map { entry -> Pair(entry.key.last(), entry.value) }
            .groupBy { it.first }
            .mapValues { entry -> entry.value.sumOf { it.second } }

    private fun applyStep(pairs: Map<String, BigInteger>): Map<String, BigInteger> {
        return pairs.flatMap { entry ->
            val rule = insertionRules[entry.key]
            if (rule == null) listOf(Pair(entry.key, entry.value))
            else {
                listOf(
                    Pair(entry.key.first() + rule, entry.value),
                    Pair(rule + entry.key.last(), entry.value)
                )
            }
        }.groupBy { it.first }
            .mapValues { entry -> entry.value.sumOf { it.second } }
    }
}
