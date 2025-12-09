package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.axisBoundLineTo
import de.devdudes.aoc.helpers.boxTo
import de.devdudes.aoc.helpers.combinations
import de.devdudes.aoc.helpers.intersects
import de.devdudes.aoc.helpers.minus
import kotlin.math.absoluteValue

class Day09 : Day(
    description = 9 - "Movie Theater",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Largest Rectangle of red tiles as corners",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 50L,
            solutionResult = 4741451444L,
            solution = { input ->
                parsePoints(input)
                    .combinations()
                    .maxOf { (pointA, pointB) ->
                        val (deltaX, deltaY) = pointA - pointB
                        (deltaX.absoluteValue + 1L) * (deltaY.absoluteValue + 1L)
                    }
            }
        )

        puzzle(
            description = 2 - "Largest Rectangle of red and green tiles",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 24L,
            solutionResult = 1_562_459_680L,
            solution = { input ->
                val points = parsePoints(input)

                val lines = (points + points.first()).windowed(size = 2, step = 1) { (a, b) -> a axisBoundLineTo b }

                points.combinations()
                    .maxOf { (pointA, pointB) ->
                        val box = pointA boxTo pointB

                        if (lines.none { line -> box.intersects(line) }) {
                            val (deltaX, deltaY) = pointA - pointB
                            (deltaX.absoluteValue + 1L) * (deltaY.absoluteValue + 1L)
                        } else {
                            0L
                        }
                    }
            }
        )
    }
)

private fun parsePoints(input: List<String>): List<Point> =
    input.map { line ->
        line.split(",")
            .map(String::toInt)
            .let { (x, y) -> Point(x, y) }
    }
