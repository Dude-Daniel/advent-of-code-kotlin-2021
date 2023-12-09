package de.devdudes.aoc.aoc2015.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import kotlin.streams.asSequence

class Day05 : Day(
    description = 5 - "Doesn't He Have Intern-Elves For This? - Number of nice Strings",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "vowels, double letter and illegal texts",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 2,
            solutionResult = 255,
            solution = { input ->
                NiceTextFinderOne(input).findNiceTexts().size
            }
        )

        puzzle(
            description = 2 - "pairs appearing twice and same letter with one letter in between",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 2,
            solutionResult = 55,
            solution = { input ->
                NiceTextFinderTwo(input).findNiceTexts().size
            }
        )
    }
)

private data class NiceTextFinderOne(val texts: List<String>) {

    fun findNiceTexts(): List<String> =
        texts.filter { text ->
            val vowelCount = text.filter { it in VOWELS }.length
            val doubleLetter = text.chars().asSequence()
                .windowed(size = 2, step = 1)
                .firstOrNull { (a, b) -> a == b }
            val illegalText = ILLEGAL_TEXTS.firstOrNull { text.contains(it) }

            vowelCount >= 3 && doubleLetter != null && illegalText == null
        }

    private companion object {
        private const val VOWELS = "aeiou"
        private val ILLEGAL_TEXTS = listOf("ab", "cd", "pq", "xy")
    }
}

private data class NiceTextFinderTwo(val texts: List<String>) {

    fun findNiceTexts(): List<String> =
        texts.filter { text ->
            // find all letter pairs
            val letterPairs = text.windowed(size = 2, step = 1).toSet()
            val doubleLetterPair = letterPairs.firstOrNull { pair ->
                // for each letter pair find one that is not overlapping
                // (always true if the letter pair exists at least three times
                // or if their starting index is two apart)
                val occurrences = Regex(pair).findAll(text).map { it.range }.toList()
                occurrences.size > 3 || (occurrences.size == 2 && occurrences[0].first + 1 < occurrences[1].first)
            }

            val letterWithOneInBetween = text.chars().asSequence()
                .windowed(size = 3, step = 1)
                .firstOrNull { (a, _, b) -> a == b }

            doubleLetterPair != null && letterWithOneInBetween != null
        }
}
