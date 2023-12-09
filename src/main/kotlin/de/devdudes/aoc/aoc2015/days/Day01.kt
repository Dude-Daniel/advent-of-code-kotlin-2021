package de.devdudes.aoc.aoc2015.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day01 : Day(
    description = 1 - "Not Quite Lisp",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Calculate Floor",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = -3L,
            solutionResult = 232L,
            solution = { input ->
                input.first().sumOf { if (it == '(') 1L else -1L }
            }
        )

        puzzle(
            description = 2 - "Character Position to enter basement",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 1,
            solutionResult = 1783,
            solution = { input ->
                var counter = 0L
                input.first().indexOfFirst {
                    counter += if (it == '(') 1L else -1L
                    counter < 0
                } + 1
            }
        )
    }
)
