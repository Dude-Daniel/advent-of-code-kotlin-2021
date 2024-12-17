package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import kotlin.math.pow

class Day17 : Day(
    description = 17 - "Chronospatial Computer",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Calculate Output",
            input = "day17",
            testInput = "day17_test",
            expectedTestResult = "5,7,3,0",
            solutionResult = "6,7,5,2,1,3,5,1,7",
            solution = { input ->
                parseProgram(input)
                    .run()
                    .joinToString(separator = ",")
            }
        )

        puzzle(
            description = 2 - "Find value of register a so the Program replicates itself",
            input = "day17",
            testInput = "day17_test",
            expectedTestResult = 117_440L,
            solutionResult = 216_549_846_240_877L,
            solution = { input ->
                parseProgram(input).findReplicatingProgram()
            }
        )
    }
)

private fun parseProgram(input: List<String>): Program {
    val (rawA, rawB, rawC, _, rawProgram) = input
    return Program(
        registerA = rawA.drop(12).toLong(),
        registerB = rawB.drop(12).toLong(),
        registerC = rawC.drop(12).toLong(),
        instructions = rawProgram.drop(9).split(",").map { it.toInt() }
    )
}

private data class Program(
    private var registerA: Long,
    private var registerB: Long,
    private var registerC: Long,
    private val instructions: List<Int>,
) {
    var pointer = 0

    fun findReplicatingProgram(): Long {
        /*
        Expecting the Program of the puzzle input:
        2,4,1,3,7,5,1,5,0,3,4,1,5,5,3,0

        2,4, -> B = A % 8                   <-- mod 8
        1,3, -> B = B xor 3
        7,5, -> C = A / 2^B
        1,5, -> B = B xor 5
        0,3, -> A = A / 2^3  ==  A = A / 8  <-- The only place where A changes. Change is always done by dividing by 8. The rest depends on A.
        4,1, -> B = B xor C                     So multiplying by 8 might help to find the solution.
        5,5, -> output B % 8                <-- mod 8
        3,0  -> jump to index 0             <-- resume from top until A == 0. This is done many times, until A = A / 8 finally results in A being 0.
         */

        var registerAValue = 0L

        // increase A until we found an output that matches on the last element of the instructions.
        while (true) {
            registerAValue++
            val output = this.copy(registerA = registerAValue).run()
            if (output.size == 1 && instructions.takeLast(output.size) == output) {
                break
            }
        }

        // When multiplying A by 8 the new output will consist a new number in the beginning and the current output: i.e. "1,2,3" mac become "5,1,2,3".
        // So each time we increase A by 8 we need to make sure all numbers match the last values of the instructions before multiplying by 8 again.
        // To find the matching digit we increase A by 1 until we found the match.
        for (digitsToMatch in 2..instructions.size) {
            registerAValue *= 8
            while (true) {
                val output = this.copy(registerA = registerAValue).run()
                if (output.size == digitsToMatch && instructions.takeLast(output.size) == output) {
                    val outputString = output.joinToString(separator = ",")
                    val missing = instructions.joinToString(separator = ",").dropLast(outputString.length)
                    val progress = missing.colored(LogColor.Red) + outputString.colored(LogColor.Green)
                    println("Found next digit: $progress - registerA: $registerAValue")
                    break
                }
                registerAValue++
            }
        }

        return registerAValue
    }

    fun run(): List<Int> {
        val output = mutableListOf<Int>()

        while (pointer <= instructions.lastIndex) {
            val operation = instructions[pointer]
            val operand = instructions[pointer + 1]

            when (operation) {
                0 -> {
                    // adv
                    registerA /= 2.0.pow(comboOperand(operand).toDouble()).toLong()
                }

                1 -> {
                    // bxl
                    registerB = registerB xor operand.toLong()
                }

                2 -> {
                    // bst
                    registerB = comboOperand(operand).mod(8L)
                }

                3 -> {
                    // jnz
                    if (registerA != 0L) {
                        pointer = operand - 2 // pointer is decreased by 2 as it will be increased at the end of the loop
                    }
                }

                4 -> {
                    // bxc
                    registerB = registerB xor registerC
                }

                5 -> {
                    // out
                    output.add(comboOperand(operand).mod(8))
                }

                6 -> {
                    // bdv
                    registerB = registerA / 2.0.pow(comboOperand(operand).toDouble()).toLong()
                }

                7 -> {
                    // cdv
                    registerC = registerA / 2.0.pow(comboOperand(operand).toDouble()).toLong()
                }
            }

            pointer += 2
        }

        return output
    }

    fun comboOperand(operand: Int): Long =
        when (operand) {
            in 0..3 -> operand.toLong()
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> error("operand not a valid combo operand: $operand")
        }
}
