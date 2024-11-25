package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.DirectionParser
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.move

class Day02 : Day(
    description = 2 - "Bathroom Security",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "9 Digit Keypad",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = "1985",
            solutionResult = "35749",
            solution = { input ->
                val keypad = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                )

                BathroomKeypad(
                    keypad = keypad,
                    input = input,
                    start = Point(1, 1),
                ).clickButtonsInDirection()
            }
        )

        puzzle(
            description = 2 - "Diamond Shaped Keypad",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = "5DB3",
            solutionResult = "9365C",
            solution = { input ->
                val keypad = listOf(
                    listOf("x", "x", "1", "x", "x"),
                    listOf("x", "2", "3", "4", "x"),
                    listOf("5", "6", "7", "8", "9"),
                    listOf("x", "A", "B", "C", "x"),
                    listOf("x", "x", "D", "x", "x"),
                )

                BathroomKeypad(
                    keypad = keypad,
                    input = input,
                    start = Point(0, 2),
                ).clickButtonsInDirection()
            }
        )
    }
)

private class BathroomKeypad(
    private val keypad: List<List<String>>,
    private val input: List<String>,
    private val start: Point,
) {

    private fun parseDirections(): List<List<Direction>> {
        val parser = DirectionParser.UDLR
        return input.map { line -> parser.parseChars(line.toCharArray().asList()) }
    }

    fun clickButtonsInDirection(): String {
        val keypad = Grid2D(keypad)
        val directions = parseDirections()

        var position = start

        return directions.map { inputDirections ->
            inputDirections.forEach { direction ->
                val nextPosition = position.move(direction = direction, distance = 1)

                val key = keypad.getOrNull(nextPosition)
                if (key != null && key != "x") {
                    position = nextPosition
                }
            }

            keypad[position]
        }.joinToString(separator = "")
    }
}
