package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day03 : Day(
    description = 3 - "Mull It Over - Sum of Multiplication Instructions",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "All Multiplication",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 161,
            solutionResult = 183_380_722,
            solution = { input ->
                val instructions = input.joinToString(separator = "")
                """mul\((\d+),(\d+)\)""".toRegex().findAll(instructions).sumOf { match ->
                    val (a, b) = match.destructured
                    a.toInt() * b.toInt()
                }
            }
        )

        puzzle(
            description = 2 - "Either Skip or Apply them",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 48,
            solutionResult = 82_733_683,
            solution = { input ->
                val instructions = input.joinToString(separator = "")
                val matches = """(do\(\))|(don't\(\))|mul\((\d+),(\d+)\)""".toRegex().findAll(instructions)

                var isValid = true
                matches.sumOf { match ->
                    val (take, ignore, a, b) = match.destructured

                    when {
                        take.isNotEmpty() -> isValid = true
                        ignore.isNotEmpty() -> isValid = false
                    }

                    if (isValid && a.isNotEmpty() && b.isNotEmpty()) {
                         a.toInt() * b.toInt()
                    } else 0
                }
            }
        )
    }
)
