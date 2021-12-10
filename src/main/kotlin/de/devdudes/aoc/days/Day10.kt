package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.math.BigInteger
import java.util.Stack
import kotlin.math.ceil

class Day10 : Day(description = 10 - "Syntax Scoring", {

    puzzle(
        description = 1 - "Corrupted Scoring",
        input = "day10",
        testInput = "day10_test",
        expectedTestResult = 26397,
        solutionResult = 339477,
        solution = { input ->
            val incompleteLines = NavigationSubsystem(input).findCorruptedLines()
            incompleteLines.map { line ->
                when (line.wrongChar) {
                    ']' -> 57
                    '>' -> 25137
                    '}' -> 1197
                    ')' -> 3
                    else -> 0
                }
            }.sum()
        }
    )

    puzzle(
        description = 2 - "Incomplete Scoring",
        input = "day10",
        testInput = "day10_test",
        expectedTestResult = 288957.toBigInteger(),
        solutionResult = 3049320156.toBigInteger(),
        solution = { input ->
            val incompleteLines = NavigationSubsystem(input).findIncompleteLines()
            val scores = incompleteLines.map { line ->
                line.missingChars.fold(BigInteger.ZERO) { acc, char ->
                    val charValue = when (char) {
                        ']' -> 2
                        '>' -> 4
                        '}' -> 3
                        ')' -> 1
                        else -> 0
                    }
                    acc.times(5.toBigInteger()) + charValue.toBigInteger()
                }
            }
            val middleIndex = ceil(scores.size.toDouble() / 2).toInt() - 1
            scores.sorted()[middleIndex]
        }
    )
})

/**
 * State of a navigation subsystem input line.
 */
private sealed class NavigationLineState() {
    object Correct : NavigationLineState()
    data class Corrupted(val wrongChar: Char) : NavigationLineState()
    data class Incomplete(val missingChars: List<Char>) : NavigationLineState()
    data class LineTooLong(val wrongChar: Char) : NavigationLineState()
}

/**
 * Navigation subsystem for parsing navigation input lines and finding errors.
 */
private class NavigationSubsystem(val lines: List<String>) {

    fun findCorruptedLines(): List<NavigationLineState.Corrupted> =
        parseAll().mapNotNull { it as? NavigationLineState.Corrupted }

    fun findIncompleteLines(): List<NavigationLineState.Incomplete> =
        parseAll().mapNotNull { it as? NavigationLineState.Incomplete }

    private fun parseAll(): List<NavigationLineState> = lines.map { parseLine(it) }
    private fun parseLine(line: String): NavigationLineState {
        val stack: Stack<Char> = Stack()

        line.toList().forEach { char ->
            if (char.isOpening) {
                stack.push(char)
            } else {
                if (stack.empty()) return NavigationLineState.LineTooLong(char)
                val openingChar = stack.pop()
                if (char != openingChar.closingChar) return NavigationLineState.Corrupted(char)
            }
        }

        return if (stack.empty()) {
            NavigationLineState.Correct
        } else {
            NavigationLineState.Incomplete(stack.map { it.closingChar }.reversed())
        }
    }
}

private val Char.isOpening: Boolean
    get() = this in listOf('[', '<', '{', '(')

private val Char.closingChar: Char
    get() = when (this) {
        '[' -> ']'
        '<' -> '>'
        '{' -> '}'
        '(' -> ')'
        else -> throw IllegalArgumentException("unsupported character: $this")
    }
