package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.concat
import de.devdudes.aoc.helpers.dropLastDigits
import de.devdudes.aoc.helpers.length

class Day02 : Day(
    description = 2 - "Gift Shop",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Invalid Shop Ids with Digits repeated exactly twice",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 1_227_775_554L,
            solutionResult = 21_139_440_284L,
            solution = { input ->
                GiftShopSolver(input).findInvalidProductIds(maxRepetitions = 2)
            }
        )

        puzzle(
            description = 2 - "Invalid Shop Ids with Digits repeated at least twice",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 4_174_379_265L,
            solutionResult = 38_731_915_928L,
            solution = { input ->
                GiftShopSolver(input).findInvalidProductIds(maxRepetitions = 100)
            }
        )
    }
)

private class GiftShopSolver(input: List<String>) {

    private val productIdRanges = input.first()
        .split(",")
        .map { range ->
            val (start, end) = range.split("-").map(String::toLong)
            start..end
        }

    fun findInvalidProductIds(maxRepetitions: Int): Long {
        var sum = 0L

        productIdRanges.map { range ->
            val startId = range.first
            val endId = range.last

            // start with 1 and iterate until the first half digis of endId (123456 -> 123)
            val start = 1
            val end = endId.dropLastDigits(endId.length() / 2)

            val invalidProductIds = mutableSetOf<Long>()

            (start..end).forEach { idPart ->
                // build id candidate by concatenating the idPart x times and check if it is in the range.
                // If the candidate grows too large or the maximum amount of repetitions of the idPart is reached -> abort
                var productIdCandidate = idPart.concat(idPart)
                var currentRepetition = 2

                while (productIdCandidate.length() <= endId.length() && currentRepetition <= maxRepetitions) {
                    if (productIdCandidate in startId..endId) {
                        invalidProductIds.add(productIdCandidate)
                    }

                    productIdCandidate = productIdCandidate.concat(idPart)
                    currentRepetition++
                }
            }

            sum += invalidProductIds.sum()
        }

        return sum
    }
}
