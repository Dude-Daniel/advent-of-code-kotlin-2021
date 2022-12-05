package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day04 : Day(
    description = 4 - "Camp Cleanup",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "number of ranges contained in another",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 2,
            solutionResult = 534,
            solution = { input ->
                val ranges = input.map { line ->
                    line.split(",").map { range ->
                        val (start, end) = range.split("-").map { it.toInt() }
                        start.rangeTo(end)
                    }
                }
                ranges.count { (first, second) ->
                    val intersections = first.intersect(second)
                    first.count() <= intersections.count() || second.count() <= intersections.count()
                }
            }
        )

        puzzle(
            description = 2 - "number of overlaps",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 4,
            solutionResult = 841,
            solution = { input ->
                val ranges = input.map { line ->
                    line.split(",").map { range ->
                        val (start, end) = range.split("-").map { it.toInt() }
                        start.rangeTo(end)
                    }
                }
                ranges.count { (first, second) ->
                    first.intersect(second).isNotEmpty()
                }
            }
        )
    }
)
