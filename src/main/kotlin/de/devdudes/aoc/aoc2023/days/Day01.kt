package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day01 : Day(
    description = 1 - "Trebuchet?! - Sum of all of the calibration values",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Digit Values",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 142,
            solutionResult = 55607,
            solution = { input ->
                input.sumOf { line ->
                    val firstDigit = line.first { it.isDigit() }
                    val secondDigit = line.last { it.isDigit() }
                    "$firstDigit$secondDigit".toInt()
                }
            }
        )

        puzzle(
            description = 2 - "Digits Values and Digits spelled out with letters",
            input = "day01",
            testInput = "day01_test_second",
            expectedTestResult = 281,
            solutionResult = 55291,
            solution = { input ->
                input.sumOf { line ->
                    val textNumbers =
                        listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

                    val digits = line.indices.mapNotNull { index ->
                        if (line[index].isDigit()) {
                            line[index].toString()
                        } else {
                            textNumbers.firstNotNullOfOrNull { numberName ->
                                if (line.substring(index).startsWith(numberName)) {
                                    textNumbers.indexOf(numberName).toString()
                                } else null
                            }
                        }
                    }

                    val firstDigit = digits.first()
                    val secondDigit = digits.last()

                    "$firstDigit$secondDigit".toInt()
                }
            }
        )
    }
)
