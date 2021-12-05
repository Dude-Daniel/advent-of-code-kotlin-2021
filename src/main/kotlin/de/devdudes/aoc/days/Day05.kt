package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day05 : Day(description = 5 - "Hydrothermal Venture", {

    puzzle(
        description = 1 - "Horizontal and Vertical",
        input = "day05",
        testInput = "day05_test",
        expectedTestResult = 5,
        solutionResult = 5084,
        solution = { input ->
            val lines = input.toHydroLines()
            val duplicates = lines.duplicatePoints(false)
            duplicates.size
        }
    )

    puzzle(
        description = 2 - "All Directions",
        input = "day05",
        testInput = "day05_test",
        expectedTestResult = 12,
        solutionResult = 17882,
        solution = { input ->
            val lines = input.toHydroLines()
            val duplicates = lines.duplicatePoints(true)
            duplicates.size
        }
    )
})

private fun List<String>.toHydroLines(): List<HydroLine> =
    this.map { line ->
        val (from, to) = line.split("->")
            .map { coordinate ->
                val (x, y) = coordinate.trim().split(",")
                HydroPoint(x.toInt(), y.toInt())
            }
        HydroLine(from, to)
    }

private data class HydroPoint(val x: Int, val y: Int)
private data class HydroLine(val from: HydroPoint, val to: HydroPoint) {
    val isHorizontal = from.y == to.y
    val isVertical = from.x == to.x
    val isDiagonal = from.x != to.x && from.y != to.y

    val points: List<HydroPoint> by lazy {
        when {
            isHorizontal -> {
                val x1 = minOf(from.x, to.x)
                val x2 = maxOf(from.x, to.x)
                (x1..x2).toList().map { HydroPoint(it, from.y) }
            }
            isVertical -> {
                val y1 = minOf(from.y, to.y)
                val y2 = maxOf(from.y, to.y)
                (y1..y2).toList().map { HydroPoint(from.x, it) }
            }
            else -> {
                // diagonal lines only appear in 45 degree

                // 1: build x values in ascending order
                val x1 = if (from.x > to.x) to else from
                val x2 = if (from.x > to.x) from else to
                val xValues = (x1.x..x2.x).toList()

                // 2: build y values in ascending order
                val y1 = minOf(x1.y, x2.y)
                val y2 = maxOf(x1.y, x2.y)
                val orderedYValues = (y1..y2).toList()

                // 3: invert y values if needed
                val yValues = if (x1.y > x2.y) orderedYValues.reversed() else orderedYValues

                // 4: merge x and y values into points
                xValues.zip(yValues).map { HydroPoint(it.first, it.second) }
            }
        }
    }
}

/**
 * Obtains all duplicate (stacking) points of all given lines.
 */
private fun List<HydroLine>.duplicatePoints(includingDiagonals: Boolean): List<HydroPoint> =
    this.filter { if (includingDiagonals) true else !it.isDiagonal }
        .flatMap { it.points }
        .groupBy { it }
        .filter { it.value.size > 1 }
        .map { it.value.first() }
