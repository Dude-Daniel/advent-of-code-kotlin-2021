package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day01 : Day(description = 1 - "Sonar Sweep", {
    puzzle(
        description = 1 - "Part One",
        input = "day01",
        testInput = "day01_test",
        expectedTestResult = 7,
        solution = { input ->
            input.map { it.toInt() }
                .zipWithNext { a, b -> b > a }
                .count { it }
        }
    )

    puzzle(
        description = 2 - "Part Two",
        input = "day01",
        testInput = "day01_test",
        expectedTestResult = 5,
        solution = { input ->
            input.map { it.toInt() }
                .windowed(3) { it.sum() }
                .zipWithNext { a, b -> b > a }
                .count { it }
        }
    )
})