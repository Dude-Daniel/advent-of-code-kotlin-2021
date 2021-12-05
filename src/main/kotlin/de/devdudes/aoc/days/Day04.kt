package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day04 : Day(description = 4 - "Giant Squid", {

    puzzle(
        description = 1 - "Bingo - First solved wins",
        input = "day04",
        testInput = "day04_test",
        expectedTestResult = 4512,
        solutionResult = 12796,
        solution = { input ->
            val numbers = input.toBoardNumberInput()
            val boardsData = input.toBingoBoards()
            val boards = BingoBoards(boardsData)

            numbers.asSequence().mapNotNull { boards.mark(it) }.first()
        }
    )

    puzzle(
        description = 2 - "Bingo - Last solved wins",
        input = "day04",
        testInput = "day04_test",
        expectedTestResult = 1924,
        solutionResult = 18063,
        solution = { input ->
            val numbers = input.toBoardNumberInput()
            val boards = input.toBingoBoards()

            val solvedBoards = boards.map { board ->
                numbers.mapIndexedNotNull { index, number ->
                    val solution = board.mark(number)
                    if (solution != null) Pair(index, solution) else null
                }.first()
            }

            solvedBoards.sortedBy { it.first }.last().second
        }
    )
})

private fun List<String>.toBingoBoards(): List<BingoBoard> =
    this.drop(2)
        .windowed(5, 6)
        .map { BingoBoard(it) }

private fun List<String>.toBoardNumberInput(): List<Int> =
    this.first()
        .split(",")
        .map { it.trim().toInt() }

/**
 * A collection of Bingo boards.
 */
private class BingoBoards(val boards: List<BingoBoard>) {
    fun mark(number: Int): Int? {
        boards.forEach { board ->
            val solution = board.mark(number)
            if (solution != null) return solution
        }
        return null
    }

    override fun toString(): String = boards.toString()
}

/**
 * Represents a bingo board.
 */
private class BingoBoard(lines: List<String>) {
    private val data: List<List<BingoValue>>

    private val unmarkedTotal
        get() = data.flatten().filter { !it.marked }.sumOf { it.value }

    init {
        data = lines.map { row ->
            row.trim().split(" ")
                .filter { it.isNotBlank() }
                .map { BingoValue(it.trim().toInt()) }
        }
    }

    /**
     * Marks the given number on all boards.
     *
     * @return The solution of the first board that was solved by the current [number]. Null in any other case.
     */
    fun mark(number: Int): Int? {
        data.flatten().forEach { bingoValue ->
            if (bingoValue.value == number) {
                bingoValue.marked = true
            }
        }

        return if (isSolved()) unmarkedTotal * number else null
    }

    private fun isSolved(): Boolean {
        data.forEach { row ->
            if (row.all { it.marked }) return true
        }

        data.transpose().forEach { row ->
            if (row.all { it.marked }) return true
        }

        return false
    }

    override fun toString(): String {
        val dataString = data.map { it.toString() }.reduce { acc, line -> "$acc\n$line" }
        return "\nBoard:\n$dataString"
    }
}

class BingoValue(val value: Int, var marked: Boolean = false) {
    override fun toString(): String = "($value, $marked)"
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}
