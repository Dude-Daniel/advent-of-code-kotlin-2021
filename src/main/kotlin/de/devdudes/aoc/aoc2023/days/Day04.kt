package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import kotlin.math.min
import kotlin.math.pow

class Day04 : Day(
    description = 4 - "Scratchcards",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum of winning numbers",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 13,
            solutionResult = 20107,
            solution = { input ->
                parseScratchCards(input)
                    .sumOf { scratchCard ->
                        val matchingNumberCount = scratchCard.getMatchingNumbers().size
                        2.0.pow(matchingNumberCount.toDouble() - 1).toInt()
                    }
            }
        )

        puzzle(
            description = 2 - "Total number of self copying scratchcards",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 30,
            solutionResult = 8172507,
            solution = { input ->
                val scratchCards = parseScratchCards(input).map { it to 1 }.toMutableList()

                scratchCards.forEachIndexed { index, (scratchCard, count) ->
                    val matchingNumberCount = scratchCard.getMatchingNumbers().size

                    // duplicate the next x cards by adding the count of the current card to their value
                    for (i in index + 1..min(index + matchingNumberCount, scratchCards.lastIndex)) {
                        scratchCards[i] = scratchCards[i].copy(second = scratchCards[i].second + count)
                    }
                }

                scratchCards.sumOf { it.second }
            }
        )
    }
)

/**
 * Parses scratchcards in the form of
 * `Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53`
 */
private fun parseScratchCards(input: List<String>): List<ScratchCard> =
    input.map { game ->
        // remove game name
        val gameInput = game.split(":").last().trim()

        // parse the number lists from the input string:
        // 1. split at |
        // 2. split each list by spaces (using regex as multiple spaces are allowed)
        // 3. convert any resulting substring to a number
        val (winningNumbers, myNumbers) = gameInput.split("|")
            .map { numbers -> numbers.trim().split(Regex(" +")).map { it.toInt() } }
        ScratchCard(winningNumbers = winningNumbers, myNumbers = myNumbers)
    }

private data class ScratchCard(
    val winningNumbers: List<Int>,
    val myNumbers: List<Int>,
) {

    fun getMatchingNumbers(): Set<Int> = myNumbers.intersect(winningNumbers.toSet())
}
