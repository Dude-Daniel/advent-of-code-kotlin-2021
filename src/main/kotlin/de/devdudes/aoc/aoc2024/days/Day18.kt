package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.PuzzleScope
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.logging.GradientColors
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.logging.gradient
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.printIndexed

class Day18 : Day(
    description = 18 - "RAM Run",
    ignored = false,
    days = {
        fun PuzzleScope.gridSize(): Int = if (isTest) 7 else 71

        puzzle(
            description = 1 - "Shortest Path to End",
            input = "day18",
            testInput = "day18_test",
            expectedTestResult = 22,
            solutionResult = 354,
            solution = { input ->
                MemoryGrid(
                    size = gridSize(),
                    cellDropPositions = parseMemoryPoints(input)
                ).findMinNumberOfStepsToEndAfterDroppingCells(if (isTest) 12 else 1024)
            }
        )

        puzzle(
            description = 2 - "First Point that blocks Path to End",
            input = "day18",
            testInput = "day18_test",
            expectedTestResult = "6,1",
            solutionResult = "36,17",
            solution = { input ->
                val point = MemoryGrid(
                    size = gridSize(),
                    cellDropPositions = parseMemoryPoints(input)
                ).findFirstPointThatBlockTheWayToTheEnd()
                "${point.x},${point.y}"
            }
        )
    }
)

private fun parseMemoryPoints(input: List<String>): List<Point> =
    input.map { line ->
        val (x, y) = line.split(",")
        Point(x.toInt(), y.toInt())
    }

private sealed class MemoryCell {
    data object Blocked : MemoryCell()
    data class Valid(var distanceFromStart: Int) : MemoryCell()
}

private data class MemoryGrid(private val size: Int, private val cellDropPositions: List<Point>) {

    private val start = Point(0, 0)
    private val end = Point(size - 1, size - 1)

    private fun Grid2D<MemoryCell>.getValidCell(point: Point): MemoryCell.Valid = get(point) as MemoryCell.Valid

    fun findMinNumberOfStepsToEndAfterDroppingCells(count: Int): Int {
        val pointsToDrop = cellDropPositions.take(count)
        val grid = MutableGrid2D(size, size) { point ->
            if (pointsToDrop.contains(point)) {
                MemoryCell.Blocked
            } else {
                MemoryCell.Valid(distanceFromStart = Int.MAX_VALUE)
            }
        }

        grid.findShortestPathToEnd()
        grid.printMemory()

        return grid.getValidCell(end).distanceFromStart
    }

    fun findFirstPointThatBlockTheWayToTheEnd(): Point {
        val grid = MutableGrid2D<MemoryCell>(size, size) { MemoryCell.Valid(distanceFromStart = Int.MAX_VALUE) }
        val cellDrops = cellDropPositions.toMutableList()
        while (true) {
            val point = cellDrops.removeFirst()
            grid[point] = MemoryCell.Blocked
            grid.findShortestPathToEnd()
            if (grid.getValidCell(end).distanceFromStart == Int.MAX_VALUE) {
                grid.printMemory(highlight = point)
                return point
            }
        }
    }

    private fun MutableGrid2D<MemoryCell>.findShortestPathToEnd() {
        // init - reset values
        filterIsInstance<MemoryCell.Valid>().forEach { it.distanceFromStart = Int.MAX_VALUE }

        getValidCell(start).distanceFromStart = 0

        // solve using dijkstra
        val pointsToVisit = mutableListOf(start)
        while (pointsToVisit.isNotEmpty()) {
            val point = pointsToVisit.removeFirst()
            val cell = getValidCell(point)

            Direction.ALL.forEach { direction ->
                val nextPoint = point.move(direction)
                when (val nextCell = getOrNull(nextPoint)) {
                    null,
                    MemoryCell.Blocked -> Unit

                    is MemoryCell.Valid -> {
                        val nextDistance = cell.distanceFromStart + 1
                        if (nextDistance < nextCell.distanceFromStart) {
                            nextCell.distanceFromStart = nextDistance
                            pointsToVisit.add(nextPoint)
                        }
                    }
                }
            }
        }
    }

    fun Grid2D<MemoryCell>.printMemory(highlight: Point? = null) {
        val maxDistance = this.filterIsInstance<MemoryCell.Valid>()
            .filter { it.distanceFromStart < Int.MAX_VALUE }
            .maxOfOrNull { it.distanceFromStart }

        val gradient = GradientColors.from(LogColor.White, LogColor.Green, 100)

        printIndexed { point, cell ->
            when (cell) {
                MemoryCell.Blocked -> "#".colored(if (point == highlight) LogColor.Red else LogColor.Blue)

                is MemoryCell.Valid -> {
                    val distance = cell.distanceFromStart
                    val color = if (distance == Int.MAX_VALUE) {
                        LogColor.Black
                    } else {
                        gradient(value = distance, range = maxDistance ?: 0, gradient = gradient)
                    }
                    ".".colored(color)
                }
            }
        }
    }
}
