package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.transpose
import kotlin.math.abs

class Day11 : Day(
    description = 11 - "Cosmic Expansion",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Expansion by 2",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 374L,
            solutionResult = 9_312_968L,
            solution = { input ->
                parseGalaxyImage(input = input, expansionFactor = 2)
                    .calculateGalaxyDistances()
                    .sumOf { it.distance }
            }
        )

        puzzle(
            description = 2 - "Expansion by 1.000.000",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 82_000_210L,
            solutionResult = 597_714_117_556,
            solution = { input ->
                parseGalaxyImage(input = input, expansionFactor = 1_000_000)
                    .calculateGalaxyDistances()
                    .sumOf { it.distance }
            }
        )
    }
)

private fun parseGalaxyImage(input: List<String>, expansionFactor: Long): GalaxyImage =
    GalaxyImage(
        points = input.map { it.toCharArray().toList() },
        expansionFactor = expansionFactor,
    )

private data class GalaxyImage(
    val points: List<List<Char>>,
    val expansionFactor: Long,
) {

    val galaxies: List<Galaxy> by lazy {
        points.mapIndexed { y, row ->
            row.mapIndexedNotNull { x, char ->
                if (char == GALAXY) Galaxy(Point(x = x, y = y)) else null
            }
        }.flatten()
    }

    private val expandingRows: List<Int> by lazy {
        points.mapIndexedNotNull { index, elements -> if (elements.contains(GALAXY)) null else index }
    }

    private val expandingColumns: List<Int> by lazy {
        points.transpose()
            .mapIndexedNotNull { index, elements -> if (elements.contains(GALAXY)) null else index }
    }

    fun calculateGalaxyDistances(): List<GalaxyDistance> =
        buildList {
            for (indexA in galaxies.indices) {
                val galaxyA = galaxies[indexA]

                // make sure to not iterate over already handled galaxy pairs
                for (indexB in indexA + 1 until galaxies.size) {
                    val galaxyB = galaxies[indexB]

                    val deltaX = abs(galaxyA.position.x - galaxyB.position.x)
                    val expandedXDelta = xExpansion(galaxyA.position.x, galaxyB.position.x)

                    val deltaY = abs(galaxyA.position.y - galaxyB.position.y)
                    val expandedYDelta = yExpansion(galaxyA.position.y, galaxyB.position.y)

                    val distance = deltaX + expandedXDelta + deltaY + expandedYDelta

                    add(
                        GalaxyDistance(
                            from = galaxyA,
                            to = galaxyB,
                            distance = distance,
                        )
                    )
                }
            }
        }

    private fun xExpansion(x1: Int, x2: Int): Long =
        expandingColumns
            .filter { it in minOf(x1, x2)..maxOf(x1, x2) }
            .size * (expansionFactor - 1)

    private fun yExpansion(y1: Int, y2: Int): Long =
        expandingRows
            .filter { it in minOf(y1, y2)..maxOf(y1, y2) }
            .size * (expansionFactor - 1)

    private companion object {
        private const val GALAXY = '#'
    }
}

private data class Galaxy(val position: Point)

private data class GalaxyDistance(
    val from: Galaxy,
    val to: Galaxy,
    val distance: Long,
)
