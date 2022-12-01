package de.devdudes.aoc.aoc2021.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

/**
 * Digit rule:
 *
 *      0:
 *     aaaa
 *    b    c
 *    b    c
 *     dddd
 *    e    f
 *    e    f
 *     gggg
 *
 * Digit patterns:
 *
 *      0:      1:      2:      3:      4:
 *     aaaa    ....    aaaa    aaaa    ....
 *    b    c  .    c  .    c  .    c  b    c
 *    b    c  .    c  .    c  .    c  b    c
 *     ....    ....    dddd    dddd    dddd
 *    e    f  .    f  e    .  .    f  .    f
 *    e    f  .    f  e    .  .    f  .    f
 *     gggg    ....    gggg    gggg    ....
 *
 *      5:      6:      7:      8:      9:
 *     aaaa    aaaa    aaaa    aaaa    aaaa
 *    b    .  b    .  .    c  b    c  b    c
 *    b    .  b    .  .    c  b    c  b    c
 *     dddd    dddd    ....    dddd    dddd
 *    .    f  e    f  .    f  e    f  .    f
 *    .    f  e    f  .    f  e    f  .    f
 *     gggg    gggg    ....    gggg    gggg
 */
class Day08 : Day(description = 8 - "Seven Segment Search", {

    puzzle(
        description = 1 - "Digits: 1, 4, 7, 8",
        input = "day08",
        testInput = "day08_test",
        expectedTestResult = 26,
        solutionResult = 369,
        solution = { input ->
            fun List<String>.filterByLength() =
                filter { digit ->
                    digit.length in SevenSegmentDigit.UNIQUE_LENGTH.map { it.segmentCount }
                }

            val entries = input.parseSevenDigitEntries()
            entries.sumOf { entry -> entry.second.filterByLength().size }
        }
    )

    puzzle(
        description = 2 - "Sum All Digits",
        input = "day08",
        testInput = "day08_test",
        expectedTestResult = 61229,
        solutionResult = 1031553,
        solution = { input ->
            val entries = input.parseSevenDigitEntries()

            val solvedEntries = entries.map { entry ->
                SevenSegmentDisplaySolver(entry.first, entry.second).solve()
            }

            solvedEntries.sum()
        }
    )
})

private fun List<String>.parseSevenDigitEntries(): List<Pair<List<String>, List<String>>> = map {
    Pair(
        it.split("|").first().trim().split(" "),
        it.split("|").last().trim().split(" "),
    )
}

private enum class SevenSegmentDigit(val value: Int, val segments: String) {
    One(1, "cf"), // length: 2
    Two(2, "acdeg"), // length: 5
    Three(3, "acdfg"), // length: 5
    Four(4, "bcdf"), // length: 4
    Five(5, "abdfg"), // length: 5
    Six(6, "abdefg"), // length: 6
    Seven(7, "acf"), // length: 3
    Eight(8, "abcdefg"), // length: 7
    Nine(9, "abcdfg"), // length: 6
    Zero(0, "abcefg"); // length: 6

    val segmentCount = segments.length

    companion object {
        val UNIQUE_LENGTH = arrayOf(One, Four, Seven, Eight)
    }
}

/**
 * Mapping of a digit from the pattern ([patternDigit]) to the [digit] of the original stricture.
 */
private data class DigitMapping(val patternDigit: Char, val digit: Char)

private class SevenSegmentDisplaySolver(signalPattern: List<String>, private val inputValue: List<String>) {

    private val signalPattern: List<List<Char>> = signalPattern.map { it.toList() }

    fun solve(): Int {
        val mappings = calculateMapping()
        val charMappings = mappings.groupBy { it.patternDigit }.mapValues { it.value.first().digit }

        val mappedInput = inputValue.applyMapping(charMappings)
        val digits = mappedInput.convertToDigits()

        return digits.fold("") { acc, digit -> acc + "$digit" }.toInt()
    }

    private fun calculateMapping(): List<DigitMapping> {
        val one = signalPattern.first { it.size == SevenSegmentDigit.One.segmentCount }
        val four = signalPattern.first { it.size == SevenSegmentDigit.Four.segmentCount }
        val seven = signalPattern.first { it.size == SevenSegmentDigit.Seven.segmentCount }
        val eight = signalPattern.first { it.size == SevenSegmentDigit.Eight.segmentCount }

        // 3: size == 5 and contains all segments of 1
        val three = signalPattern.first { it.size == 5 && it.containsAll(one) }

        // 5: size == 5 and contains all segments of (4 which are not in 1)
        val segmentsOfFive = four.filter { it !in one }
        val five = signalPattern.first { it.size == 5 && it.containsAll(segmentsOfFive) }

        // 2: size == 5 and not (3 or 5)
        val two = signalPattern.first { it.size == 5 && !(it == three || it == five) }

        // 6: size == 6 and does not contain all segments of 7
        val six = signalPattern.first { it.size == 6 && !it.containsAll(seven) }

        // 9: size == 6 and contains all segments of 5 but is not 6
        val nine = signalPattern.first { it.size == 6 && it.containsAll(five) && it != six }

        // 0: size == 6 and not (6 or 9)
        val zero = signalPattern.first { it.size == 6 && !(it == six || it == nine) }

        return listOf(
            // a: contained in 7 but not 1
            DigitMapping(seven.first { it !in one }, 'a'),

            // b: contained in 4 but not 3
            DigitMapping(four.first { it !in three }, 'b'),

            // c: contained in 1 and 2
            DigitMapping(one.first { it in two }, 'c'),

            // d: contained in 8 but not 0
            DigitMapping(eight.first { it !in zero }, 'd'),

            // e: contained in 0 but not 9
            DigitMapping(zero.first { it !in nine }, 'e'),

            // f: contained in 1 but not 2
            DigitMapping(one.first { it !in two }, 'f'),

            // g: contained in 9 but not in 4 or 7
            DigitMapping(nine.first { it !in four && it !in seven }, 'g')
        )
    }

    private fun List<String>.applyMapping(mapping: Map<Char, Char>): List<String> {
        return map { digits ->
            digits.toList()
                .map { char -> mapping[char]!! }
                .sorted()
                .joinToString("")
        }
    }

    private fun List<String>.convertToDigits(): List<Int> =
        map { digit ->
            SevenSegmentDigit.values().first { digit == it.segments }.value
        }
}
