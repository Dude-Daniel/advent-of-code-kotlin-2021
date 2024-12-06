package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen

class Day05 : Day(
    description = 5 - "Print Queue",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum correctly ordered Page Updates",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 143,
            solutionResult = 5_651,
            solution = { input ->
                parsePrintQueueData(input).calculateSumOfValidUpdates()
            }
        )

        puzzle(
            description = 2 - "Recalculate and Sum incorrectly ordered Page Updates",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 123,
            solutionResult = 4_743,
            solution = { input ->
                parsePrintQueueData(input).calculateSumOfInvalidUpdates()
            }
        )
    }
)

private fun parsePrintQueueData(input: List<String>): PrintQueueData {
    val (orderingRulesRaw, updatesRaw) = input.splitWhen { it.isEmpty() }

    val orderingRules = orderingRulesRaw.map {
        val (from, to) = it.split("|")
        from.toInt() to to.toInt()
    }

    val updates = updatesRaw.map { it.split(",").map(String::toInt) }

    return PrintQueueData(orderingRules = orderingRules, updates = updates)
}

private data class PrintQueueData(
    val orderingRules: List<Pair<Int, Int>>,
    val updates: List<List<Int>>,
) {

    private fun filterUpdates(valid: Boolean): List<List<Int>> =
        updates.filter { pages ->
            valid == pages.zipWithNext().all { orderingRule -> orderingRules.contains(orderingRule) }
        }

    fun calculateSumOfValidUpdates(): Int = filterUpdates(true).sumOf { pages -> pages[pages.lastIndex / 2] }

    fun calculateSumOfInvalidUpdates(): Int =
        filterUpdates(false)
            .map { pages ->
                // resort the pages by checking how two compared values are defined by the ordering rules
                pages.sortedWith { first, second ->
                    if (orderingRules.contains(first to second)) -1 else 1
                }
            }
            .sumOf { pages -> pages[pages.lastIndex / 2] }
}
