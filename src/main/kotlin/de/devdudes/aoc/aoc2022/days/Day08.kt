package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day08 : Day(
    description = 8 - "Treetop Tree House",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of visible trees",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 21,
            solutionResult = 1708,
            solution = { input ->
                parseTreeGrid(input)
                    .findVisibleTrees()
            }
        )

        puzzle(
            description = 2 - "Highest scenic score",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 8,
            solutionResult = 504000,
            solution = { input ->
                parseTreeGrid(input)
                    .findScenicScore()
            }
        )
    }
)

private fun parseTreeGrid(input: List<String>): TreeGrid {
    val values = input.map { line ->
        line.chunked(size = 1).map { it.toInt() }
    }

    return TreeGrid(values)
}

private data class TreeGrid(val heights: List<List<Int>>) {

    val rows: Int by lazy { heights.size }
    val columns: Int by lazy { heights.first().size }

    fun findVisibleTrees(): Int {
        var visibleTrees = 0

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                if (isVisible(row = row, column = column)) visibleTrees += 1
            }
        }

        return visibleTrees
    }

    private fun isVisible(row: Int, column: Int): Boolean {
        val height = heights[row][column]

        return isHighestToLeft(height = height, row = row, column = column)
                || isHighestToRight(height = height, row = row, column = column)
                || isHighestToTop(height = height, row = row, column = column)
                || isHighestToBottom(height = height, row = row, column = column)
    }

    private fun isHighest(
        height: Int,
        indexProgression: IntProgression,
        heightOf: (index: Int) -> Int,
    ): Boolean {
        var isHighest = true
        for (index in indexProgression) {
            if (height <= heightOf(index)) {
                isHighest = false
                break
            }
        }
        return isHighest
    }

    private fun isHighestToLeft(height: Int, row: Int, column: Int): Boolean =
        isHighest(
            height = height,
            indexProgression = column - 1 downTo 0,
            heightOf = { index -> heights[row][index] },
        )

    private fun isHighestToRight(height: Int, row: Int, column: Int): Boolean =
        isHighest(
            height = height,
            indexProgression = column + 1 until columns,
            heightOf = { index -> heights[row][index] },
        )

    private fun isHighestToTop(height: Int, row: Int, column: Int): Boolean =
        isHighest(
            height = height,
            indexProgression = row - 1 downTo 0,
            heightOf = { index -> heights[index][column] },
        )

    private fun isHighestToBottom(height: Int, row: Int, column: Int): Boolean =
        isHighest(
            height = height,
            indexProgression = row + 1 until rows,
            heightOf = { index -> heights[index][column] },
        )

    private fun findVisibleNeighbors(
        height: Int,
        indexProgression: IntProgression,
        heightOf: (index: Int) -> Int,
    ): Int {
        var count = 0
        for (index in indexProgression) {
            count += 1
            if (height <= heightOf(index)) {
                break
            }
        }
        return count
    }

    private fun findVisibleNeighborsToLeft(height: Int, row: Int, column: Int): Int =
        findVisibleNeighbors(
            height = height,
            indexProgression = column - 1 downTo 0,
            heightOf = { index -> heights[row][index] },
        )

    private fun findVisibleNeighborsToRight(height: Int, row: Int, column: Int): Int =
        findVisibleNeighbors(
            height = height,
            indexProgression = column + 1 until columns,
            heightOf = { index -> heights[row][index] },
        )

    private fun findVisibleNeighborsToTop(height: Int, row: Int, column: Int): Int =
        findVisibleNeighbors(
            height = height,
            indexProgression = row - 1 downTo 0,
            heightOf = { index -> heights[index][column] },
        )

    private fun findVisibleNeighborsToBottom(height: Int, row: Int, column: Int): Int =
        findVisibleNeighbors(
            height = height,
            indexProgression = row + 1 until rows,
            heightOf = { index -> heights[index][column] },
        )

    fun findScenicScore(): Int {
        var maxScenicScore = 0

        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val score = calculateScenicScore(row = row, column = column)
                if (score > maxScenicScore) maxScenicScore = score
            }
        }

        return maxScenicScore
    }

    private fun calculateScenicScore(row: Int, column: Int): Int {
        val height = heights[row][column]

        return findVisibleNeighborsToLeft(height = height, row = row, column = column) *
                findVisibleNeighborsToRight(height = height, row = row, column = column) *
                findVisibleNeighborsToTop(height = height, row = row, column = column) *
                findVisibleNeighborsToBottom(height = height, row = row, column = column)
    }
}
