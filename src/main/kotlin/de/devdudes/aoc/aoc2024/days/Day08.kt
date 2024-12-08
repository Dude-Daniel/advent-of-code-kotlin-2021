package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.mapValuesIndexedNotNull
import de.devdudes.aoc.helpers.minus
import de.devdudes.aoc.helpers.plus
import de.devdudes.aoc.helpers.print
import de.devdudes.aoc.helpers.toGrid

class Day08 : Day(
    description = 8 - "Resonant Collinearity - Number of Antinodes",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Neighbor Antinodes",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 14,
            solutionResult = 273,
            solution = { input ->
                AntennaGrid(input).countNeighborAntinodes()
            }
        )

        puzzle(
            description = 2 - "All Antinodes in line",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 34,
            solutionResult = 1_017,
            solution = { input ->
                AntennaGrid(input).countAllPossibleAntinodesInLine()
            }
        )
    }
)

private class AntennaGrid(private val input: List<String>) {

    private fun getGrid(): Grid2D<Char> = input.map { line -> line.toCharArray().toList() }.toGrid()

    private fun Grid2D<Char>.antennaCoordinates(): Map<Char, List<Point>> =
        mapValuesIndexedNotNull { index, char -> if (char != '.') char to index else null }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })

    private fun Map<Char, List<Point>>.forEachAntennaPair(action: (pointA: Point, pointB: Point) -> Unit) {
        forEach { (_, points) ->
            points.forEachIndexed { index, pointA ->
                points.drop(index + 1).forEach { pointB ->
                    action(pointA, pointB)
                }
            }
        }
    }

    private fun Grid2D<Boolean>.printAntinodes() = print { if (!it) "." else "#" }

    fun countNeighborAntinodes(): Int {
        val grid = getGrid()
        val antennasWithCoordinates = grid.antennaCoordinates()

        val antinodeGrid = MutableGrid2D(grid.columns, grid.rows) { false }
        antennasWithCoordinates.forEachAntennaPair { pointA, pointB ->
            val delta = pointB - pointA

            val antinodePointA = pointA - delta
            antinodeGrid.replace(antinodePointA, true)

            val antinodePointB = pointB + delta
            antinodeGrid.replace(antinodePointB, true)
        }

        antinodeGrid.printAntinodes()

        return antinodeGrid.count { it }
    }

    fun countAllPossibleAntinodesInLine(): Int {
        val grid = getGrid()
        val antennasWithCoordinates = grid.antennaCoordinates()

        val antinodeGrid = MutableGrid2D(grid.columns, grid.rows) { false }

        antennasWithCoordinates.forEachAntennaPair { pointA, pointB ->
            val delta = pointB - pointA

            var previousPoint = pointA - delta
            while (grid.contains(previousPoint)) {
                antinodeGrid.replace(previousPoint, true)
                previousPoint -= delta
            }

            var nextPoint = pointB + delta
            while (grid.contains(nextPoint)) {
                antinodeGrid.replace(nextPoint, true)
                nextPoint += delta
            }
        }

        // Place antinodes for antennas which appear more then once
        antennasWithCoordinates.forEach { (_, points) ->
            if (points.size > 1) {
                points.forEach { antinodeGrid.replace(it, true) }
            }
        }

        antinodeGrid.printAntinodes()

        return antinodeGrid.count { it }
    }
}
