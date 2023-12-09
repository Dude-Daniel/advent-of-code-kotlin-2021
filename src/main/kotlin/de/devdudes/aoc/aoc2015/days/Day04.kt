package de.devdudes.aoc.aoc2015.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.md5

class Day04 : Day(
    description = 4 - "The Ideal Stocking Stuffer",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Hash starting with 5 zeros",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 1_048_970,
            solutionResult = 254_575,
            solution = { input ->
                mineAdventCoin(secretKey = input.first(), startingZeros = 5)
            }
        )

        puzzle(
            description = 2 - "Hash starting with 6 zeros",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 5_714_438,
            solutionResult = 1_038_736,
            solution = { input ->
                mineAdventCoin(secretKey = input.first(), startingZeros = 6)
            }
        )
    }
)

private fun mineAdventCoin(secretKey: String, startingZeros: Int): Int {
    var number = 1
    var numberFound = false
    val startingString = List(startingZeros) { "0" }.joinToString(separator = "")

    // add number to the secretKey until a hash is produced that starts with the specified amount of zeros
    do {
        val hash = "$secretKey$number".md5()
        if (hash.startsWith(startingString)) {
            numberFound = true
        } else {
            number += 1
        }
    } while (!numberFound)

    return number
}
