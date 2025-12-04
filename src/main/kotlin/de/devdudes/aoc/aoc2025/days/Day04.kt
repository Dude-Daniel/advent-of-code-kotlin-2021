package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.mapValuesIndexedNotNull
import de.devdudes.aoc.helpers.neighborEight
import de.devdudes.aoc.helpers.printIndexed
import de.devdudes.aoc.helpers.subtractMatching
import de.devdudes.aoc.helpers.toGrid
import de.devdudes.aoc.helpers.toMutableGrid

class Day04 : Day(
    description = 4 - "Printing Department",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of Paper Rolls that can be removed",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 13,
            solutionResult = 1395,
            solution = { input ->
                PaperRollGridSolver(input).countPaperRollsThatCanBeRemoved()
            }
        )

        puzzle(
            description = 2 - "Remove as much Paper Rolls as possible",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 43,
            solutionResult = 8451,
            solution = { input ->
                PaperRollGridSolver(input).countMaximumRemovableRolls()
            }
        )
    }
)

private class PaperRollGridSolver(input: List<String>) {

    sealed class GridState {
        data object Empty : GridState()
        data object PaperRoll : GridState()
    }

    private fun Grid2D<GridState>.printPaperRolls(highlightedRolls: List<Point> = emptyList()): Grid2D<GridState> =
        printIndexed { point, value ->
            when (value) {
                GridState.Empty -> ".".colored(LogColor.White)
                GridState.PaperRoll -> "@".colored(if (highlightedRolls.contains(point)) LogColor.Green else LogColor.White)
            }
        }

    private val initialGrid = input.map { line ->
        line.toCharArray().map {
            when (it) {
                '@' -> GridState.PaperRoll
                else -> GridState.Empty
            }
        }
    }.toGrid()

    fun countPaperRollsThatCanBeRemoved(): Int {
        val pointsToRemove = initialGrid.findRemovableRollPositions()

        initialGrid.printPaperRolls(highlightedRolls = pointsToRemove)

        return pointsToRemove.count()
    }

    fun countMaximumRemovableRolls(): Int {
        val minimizedGrid = removeAsManyRollsAsPossible(initialGrid)
        val gridWithRemovedRolls = initialGrid.subtractMatching(other = minimizedGrid, default = GridState.Empty)

        val removedRollPositions = gridWithRemovedRolls.mapValuesIndexedNotNull { point, value -> point.takeIf { value == GridState.PaperRoll } }
        initialGrid.printPaperRolls(highlightedRolls = removedRollPositions)

        return gridWithRemovedRolls.count { it == GridState.PaperRoll }
    }

    private fun removeAsManyRollsAsPossible(grid: Grid2D<GridState>): Grid2D<GridState> {
        val pointsToRemove = grid.findRemovableRollPositions()
        return if (pointsToRemove.isNotEmpty()) {
            val newGrid = grid.toMutableGrid().apply {
                pointsToRemove.forEach { point -> set(point, GridState.Empty) }
            }

            removeAsManyRollsAsPossible(newGrid)
        } else {
            grid
        }
    }

    private fun Grid2D<GridState>.findRemovableRollPositions(): List<Point> =
        mapValuesIndexedNotNull { point, value ->
            when (value) {
                GridState.Empty -> null
                GridState.PaperRoll -> {
                    val neighbors = neighborEight(center = point, emptyValue = GridState.Empty)
                    if (neighbors.count { it == GridState.PaperRoll } < 4) {
                        point
                    } else {
                        null
                    }
                }
            }
        }
}
