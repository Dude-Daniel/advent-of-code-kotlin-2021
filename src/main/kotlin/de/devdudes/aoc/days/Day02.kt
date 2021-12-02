package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day02 : Day(description = 2 - "Dive!", {
    puzzle(
        description = 1 - "Part One",
        input = "day02",
        testInput = "day02_test",
        expectedTestResult = 150,
        solution = { input ->
            class Course(private var position: Int = 0, private var depth: Int = 0) {
                val result get() = position * depth

                fun navigate(command: SubmarineCommand) {
                    when (command) {
                        is SubmarineCommand.Forward -> position += command.amount
                        is SubmarineCommand.Down -> depth += command.amount
                        is SubmarineCommand.Up -> depth -= command.amount
                    }
                }
            }

            val course = Course()
            input.toSubmarineCommands().forEach { course.navigate(it) }
            course.result
        }
    )

    puzzle(
        description = 2 - "Part Two",
        input = "day02",
        testInput = "day02_test",
        expectedTestResult = 900,
        solution = { input ->
            class Course(private var position: Int = 0, private var depth: Int = 0, private var aim: Int = 0) {
                val result get() = position * depth

                fun navigate(command: SubmarineCommand) {
                    when (command) {
                        is SubmarineCommand.Forward -> {
                            position += command.amount
                            depth += aim * command.amount
                        }
                        is SubmarineCommand.Down -> aim += command.amount
                        is SubmarineCommand.Up -> aim -= command.amount
                    }
                }
            }

            val course = Course()
            input.toSubmarineCommands().forEach { course.navigate(it) }
            course.result
        }
    )
})

private sealed class SubmarineCommand {
    data class Forward(val amount: Int) : SubmarineCommand()
    data class Down(val amount: Int) : SubmarineCommand()
    data class Up(val amount: Int) : SubmarineCommand()
}

private fun List<String>.toSubmarineCommands(): List<SubmarineCommand> = mapNotNull {
    val (command, value) = it.split(" ")
    when (command) {
        "forward" -> SubmarineCommand.Forward(value.toInt())
        "down" -> SubmarineCommand.Down(value.toInt())
        "up" -> SubmarineCommand.Up(value.toInt())
        else -> null
    }
}
