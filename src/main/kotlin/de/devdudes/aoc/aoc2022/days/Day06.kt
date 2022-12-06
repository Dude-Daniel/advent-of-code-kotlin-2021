package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day06 : Day(
    description = 6 - "Tuning Trouble",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "marker position of size 4",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 7,
            solutionResult = 1651,
            solution = { input ->
                findIndexOfMarker(
                    input = input.first(),
                    distinctMarkerSize = 4,
                )
            }
        )

        puzzle(
            description = 2 - "marker position of size 14",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 19,
            solutionResult = 3837,
            solution = { input ->
                findIndexOfMarker(
                    input = input.first(),
                    distinctMarkerSize = 14,
                )
            }
        )
    }
)

private fun findIndexOfMarker(input: String, distinctMarkerSize: Int): Int =
    input.windowed(
        size = distinctMarkerSize,
        step = 1,
    ).indexOfFirst { data ->
        data.toSet().size == distinctMarkerSize
    } + distinctMarkerSize // add index "hidden" by windowed
