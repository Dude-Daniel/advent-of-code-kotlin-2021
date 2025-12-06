package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.merge
import de.devdudes.aoc.helpers.splitWhen
import de.devdudes.aoc.helpers.valueCount

class Day05 : Day(
    description = 5 - "Cafeteria",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number on fresh Ingredient Ids",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 3,
            solutionResult = 511,
            solution = { input ->
                parseInventoryDatabase(input).countValidIngredients()
            }
        )

        puzzle(
            description = 2 - "Number of maximum fresh Ingredient Ids",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 14L,
            solutionResult = 350_939_902_751_909L,
            solution = { input ->
                parseInventoryDatabase(input).maxIngredientCount()
            }
        )
    }
)

private data class InventoryDatabase(
    val validIdRanges: List<LongRange>,
    val ingredientIds: List<Long>,
) {
    fun countValidIngredients(): Int = ingredientIds.count { id -> validIdRanges.any { it.contains(id) } }

    fun maxIngredientCount(): Long = mergeAllRangesRecursively(validIdRanges).sumOf { it.valueCount }

    private fun mergeAllRangesRecursively(ranges: Collection<LongRange>): Set<LongRange> {
        val resultingRanges = mutableSetOf<LongRange>()

        val sortedRanges = ranges.sortedBy { it.first }
        sortedRanges.forEach { range ->

            // expand current range by merging it with all other available ranges
            val newRange = sortedRanges.fold(range) { acc, nextRange ->
                when (val mergedRange = acc.merge(nextRange)) {
                    null -> acc
                    else -> mergedRange
                }
            }

            resultingRanges.add(newRange)
        }

        // repeating the merge process until no more merges occur
        return if (resultingRanges == ranges.toSet()) {
            resultingRanges
        } else {
            mergeAllRangesRecursively(resultingRanges)
        }
    }
}

private fun parseInventoryDatabase(input: List<String>): InventoryDatabase {
    val (rawRanges, rawIds) = input.splitWhen(String::isEmpty)

    val ranges = rawRanges.map { rawRange ->
        val (from, to) = rawRange.split("-").map(String::toLong)
        from..to
    }

    return InventoryDatabase(
        validIdRanges = ranges,
        ingredientIds = rawIds.map(String::toLong),
    )
}
