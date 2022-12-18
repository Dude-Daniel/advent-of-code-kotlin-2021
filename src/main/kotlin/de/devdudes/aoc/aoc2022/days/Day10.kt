package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day10 : Day(
    description = 10 - "Cathode-Ray Tube",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum of 6 Signal strengths",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 13140,
            solutionResult = 12540,
            solution = { input ->
                val commands = parseCathodeRayCommands(input)
                val cpu = CathodeRayCPU(commands)
                cpu.compute(19) // go to start of cycle: 40
                cpu.recordSignalStrength()
                cpu.compute(40) // go to start of cycle: 60
                cpu.recordSignalStrength()
                cpu.compute(40) // go to start of cycle: 100
                cpu.recordSignalStrength()
                cpu.compute(40) // go to start of cycle: 140
                cpu.recordSignalStrength()
                cpu.compute(40) // go to start of cycle: 180
                cpu.recordSignalStrength()
                cpu.compute(40) // go to start of cycle: 220
                cpu.recordSignalStrength()
                cpu.signalStrength
            }
        )

        puzzle(
            description = 2 - "Draw on CRT",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = buildString {
                append("##..##..##..##..##..##..##..##..##..##..\n")
                append("###...###...###...###...###...###...###.\n")
                append("####....####....####....####....####....\n")
                append("#####.....#####.....#####.....#####.....\n")
                append("######......######......######......####\n")
                append("#######.......#######.......#######.....")
            },
            solutionResult = buildString {
                // FECZELHE
                append("####.####..##..####.####.#....#..#.####.\n")
                append("#....#....#..#....#.#....#....#..#.#....\n")
                append("###..###..#......#..###..#....####.###..\n")
                append("#....#....#.....#...#....#....#..#.#....\n")
                append("#....#....#..#.#....#....#....#..#.#....\n")
                append("#....####..##..####.####.####.#..#.####.")
            },
            solution = { input ->
                val commands = parseCathodeRayCommands(input)
                val cpu = CathodeRayCPU(commands)
                cpu.computeCRT()
                cpu.getFormattedCRT()
            }
        )
    }
)

private fun parseCathodeRayCommands(input: List<String>): List<CathodeRayCommand> =
    input.map { line ->
        when (line) {
            "noop" -> CathodeRayCommand.Noop
            else -> {
                val value = line.split(" ").last().toInt()
                CathodeRayCommand.AddX(value)
            }
        }
    }

private sealed class CathodeRayCommand {
    object Noop : CathodeRayCommand()
    data class AddX(val value: Int) : CathodeRayCommand()
}

private class CathodeRayCPU(private val commands: List<CathodeRayCommand>) {
    private var cycle = 1
    private var xRegister = 1
    var signalStrength = 0
        private set

    private val crtValues = mutableListOf<String>()
    private val processedCommandValues = mutableListOf<Int>()

    private var commandCycle: Int = 0
    private var command: CathodeRayCommand? = null
    private var commandIndex = 0

    fun compute(cycles: Int) {
        repeat(cycles) {
            cycle += 1

            when (val currentCommand = getCurrentOrNextCommand()) {
                is CathodeRayCommand.AddX -> {
                    if (commandCycle == 0) commandCycle += 1
                    else {
                        xRegister += currentCommand.value
                        processedCommandValues.add(currentCommand.value)
                        commandCycle = 0
                        command = null
                    }
                }

                CathodeRayCommand.Noop -> {
                    command = null
                }
            }
        }
    }

    private fun getCurrentOrNextCommand(): CathodeRayCommand {
        val currentCommand = command
        return if (currentCommand == null) {
            val nextCommand = commands[commandIndex]
            commandIndex += 1
            command = nextCommand
            commandCycle = 0
            nextCommand
        } else {
            currentCommand
        }
    }

    fun recordSignalStrength() {
        signalStrength += cycle * xRegister
    }

    fun computeCRT() {
        while (commandIndex < commands.size) {
            val index = (cycle - 1) % 40
            val crtValue = if (index >= xRegister - 1 && index <= xRegister + 1) "#" else "."
            crtValues.add(crtValue)
            compute(1)
        }
    }

    fun getFormattedCRT(): String {
        return crtValues.chunked(40)
            .map { it.joinToString(separator = "") }
            .joinToString("\n")
    }
}
