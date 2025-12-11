package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.combinations

class Day10 : Day(
    description = 10 - "Factory",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Fewest Button presses to turn on the lights",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 7,
            solutionResult = 520,
            solution = { input ->
                parseFactory(input).calculateFewestButtonPressesToConfigureLightDiagram()
            }
        )

        puzzle(
            description = 2 - "Fewest Button presses to reach jolatage values",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 33,
            solutionResult = 20626,
            solution = { input ->
                // Solving this puzzle requires so solve linear equations with multiple variables and multiple possible resulting value combinations.
                // This is actually quite hard. Simply iterating over every possible combination of input parameters takes too much time.
                // Therefore z3 is used here.

                // 1. uncomment this line to print the z3 commands
                // parseFactory(input).machines.forEach { machineConfig ->
                //     println(machineConfig.buildZ3Commands())
                // }

                // 2. parsing the results of z3 and solving the puzzle
                parseZ3Result(if (isTest) z3ResponseTestData else z3ResponsePuzzleData).sum()
            }
        )
    }
)

private fun parseFactory(input: List<String>): Factory =
    input.map { line ->
        val lightDiagramEndIndex = line.indexOf("]")
        val joltageStartIndex = line.indexOf("{")

        val lightDiagramValues = line.substring(startIndex = 1, endIndex = lightDiagramEndIndex)
            .toCharArray()
            .map { it == '#' }

        val buttonWiring = line.substring(startIndex = lightDiagramEndIndex + 2, endIndex = joltageStartIndex - 1)
            .split(" ")
            .map { wiring ->
                wiring.drop(1)
                    .dropLast(1)
                    .split(",")
                    .map(String::toInt)
                    .let(::ButtonWiring)
            }

        val joltageRequirements = line.substring(startIndex = joltageStartIndex + 1, endIndex = line.lastIndex)
            .split(",")
            .map(String::toInt)

        FactoryMachineConfig(
            lightDiagram = LightDiagram(lightDiagramValues),
            buttonWiring = buttonWiring,
            joltageRequirements = JoltageRequirement(joltageRequirements),
        )
    }.let(::Factory)

private data class FactoryMachineConfig(
    val lightDiagram: LightDiagram,
    val buttonWiring: List<ButtonWiring>,
    val joltageRequirements: JoltageRequirement,
) {

    /**
     * Generates commands for z3 which can be run using: https://microsoft.github.io/z3guide/playground/Freeform%20Editing
     * Output for first test data:
     *
     * ```
     * (reset)
     * (declare-const k0 Int)
     * (assert (>= k0 0))
     * (declare-const k1 Int)
     * (assert (>= k1 0))
     * (declare-const k2 Int)
     * (assert (>= k2 0))
     * (declare-const k3 Int)
     * (assert (>= k3 0))
     * (declare-const k4 Int)
     * (assert (>= k4 0))
     * (declare-const k5 Int)
     * (assert (>= k5 0))
     * (assert (= (+ k4 k5) 3))
     * (assert (= (+ k1 k5) 5))
     * (assert (= (+ k2 k3 k4) 4))
     * (assert (= (+ k0 k1 k3) 7))
     * (declare-const total Int)
     * (assert (= total (+ k0 k1 k2 k3 k4 k5)))
     * (minimize total)
     * (check-sat)
     * (get-objectives)
     * ```
     */
    fun buildZ3Commands(): String = buildString {
        appendLine("(reset)")
        buttonWiring.forEachIndexed { index, wiring ->
            appendLine("(declare-const k$index Int)")
            appendLine("(assert (>= k$index 0))")
        }

        joltageRequirements.joltages.forEachIndexed { indexJoltage, joltage ->
            append("(assert (= (+")
            buttonWiring.forEachIndexed { indexWiring, wiring ->
                if (wiring.indices.contains(indexJoltage)) {
                    append(" k$indexWiring")
                }
            }
            appendLine(") $joltage))")
        }

        appendLine("(declare-const total Int)")
        append("(assert (= total (+")
        buttonWiring.indices.forEach { append(" k$it") }
        appendLine(")))")

        appendLine("(minimize total)")
        appendLine("(check-sat)")
        appendLine("(get-objectives)")
    }
}

/**
 * Parses the z3 results of the form:
 *
 * ```
 * sat
 * (objectives
 *  (total 10)
 * )
 * sat
 * (objectives
 *  (total 12)
 * )
 * sat
 * (objectives
 *  (total 11)
 * )
 * ```
 */
fun parseZ3Result(z3Response: String): List<Int> =
    z3Response.split("\n")
        .filter { it.contains("total") }
        .map { filterText ->
            filterText.drop(8).dropLast(1).toInt()
        }

private data class ButtonWiring(val indices: List<Int>)

private data class LightDiagram(val values: List<Boolean>) {
    fun toggle(wirings: List<ButtonWiring>): LightDiagram = wirings.fold(this) { diagram, wiring -> diagram.toggle(wiring) }

    fun toggle(wiring: ButtonWiring): LightDiagram =
        values.mapIndexed { index, state ->
            when (wiring.indices.contains(index)) {
                true -> !state
                false -> state
            }
        }.let(::LightDiagram)

    companion object {
        fun initial(size: Int): LightDiagram = List(size) { false }.let(::LightDiagram)
    }
}

private data class JoltageRequirement(val joltages: List<Int>)

private class Factory(val machines: List<FactoryMachineConfig>) {
    fun calculateFewestButtonPressesToConfigureLightDiagram(): Int =
        machines.sumOf { machine ->
            var found = false
            var buttonCount = 1
            val initialDiagram = LightDiagram.initial(machine.lightDiagram.values.size)
            while (!found) {
                found = machine.buttonWiring.combinations(buttonCount)
                    .any { wirings ->
                        initialDiagram.toggle(wirings) == machine.lightDiagram
                    }

                if (!found) buttonCount++
            }
            buttonCount
        }
}

private const val z3ResponseTestData = """
sat
(objectives
 (total 10)
)
sat
(objectives
 (total 12)
)
sat
(objectives
 (total 11)
)
"""

private const val z3ResponsePuzzleData = """
sat
(objectives
 (total 49)
)
sat
(objectives
 (total 287)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 81)
)
sat
(objectives
 (total 4)
)
sat
(objectives
 (total 234)
)
sat
(objectives
 (total 68)
)
sat
(objectives
 (total 35)
)
sat
(objectives
 (total 112)
)
sat
(objectives
 (total 102)
)
sat
(objectives
 (total 86)
)
sat
(objectives
 (total 69)
)
sat
(objectives
 (total 135)
)
sat
(objectives
 (total 60)
)
sat
(objectives
 (total 30)
)
sat
(objectives
 (total 53)
)
sat
(objectives
 (total 291)
)
sat
(objectives
 (total 95)
)
sat
(objectives
 (total 239)
)
sat
(objectives
 (total 196)
)
sat
(objectives
 (total 147)
)
sat
(objectives
 (total 59)
)
sat
(objectives
 (total 89)
)
sat
(objectives
 (total 196)
)
sat
(objectives
 (total 188)
)
sat
(objectives
 (total 59)
)
sat
(objectives
 (total 89)
)
sat
(objectives
 (total 122)
)
sat
(objectives
 (total 76)
)
sat
(objectives
 (total 218)
)
sat
(objectives
 (total 198)
)
sat
(objectives
 (total 214)
)
sat
(objectives
 (total 185)
)
sat
(objectives
 (total 186)
)
sat
(objectives
 (total 29)
)
sat
(objectives
 (total 160)
)
sat
(objectives
 (total 27)
)
sat
(objectives
 (total 194)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 262)
)
sat
(objectives
 (total 65)
)
sat
(objectives
 (total 132)
)
sat
(objectives
 (total 28)
)
sat
(objectives
 (total 98)
)
sat
(objectives
 (total 55)
)
sat
(objectives
 (total 63)
)
sat
(objectives
 (total 72)
)
sat
(objectives
 (total 71)
)
sat
(objectives
 (total 47)
)
sat
(objectives
 (total 59)
)
sat
(objectives
 (total 219)
)
sat
(objectives
 (total 80)
)
sat
(objectives
 (total 82)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 135)
)
sat
(objectives
 (total 22)
)
sat
(objectives
 (total 67)
)
sat
(objectives
 (total 101)
)
sat
(objectives
 (total 24)
)
sat
(objectives
 (total 43)
)
sat
(objectives
 (total 82)
)
sat
(objectives
 (total 182)
)
sat
(objectives
 (total 99)
)
sat
(objectives
 (total 43)
)
sat
(objectives
 (total 50)
)
sat
(objectives
 (total 25)
)
sat
(objectives
 (total 190)
)
sat
(objectives
 (total 50)
)
sat
(objectives
 (total 55)
)
sat
(objectives
 (total 87)
)
sat
(objectives
 (total 262)
)
sat
(objectives
 (total 87)
)
sat
(objectives
 (total 120)
)
sat
(objectives
 (total 63)
)
sat
(objectives
 (total 39)
)
sat
(objectives
 (total 111)
)
sat
(objectives
 (total 62)
)
sat
(objectives
 (total 131)
)
sat
(objectives
 (total 64)
)
sat
(objectives
 (total 100)
)
sat
(objectives
 (total 94)
)
sat
(objectives
 (total 14)
)
sat
(objectives
 (total 37)
)
sat
(objectives
 (total 244)
)
sat
(objectives
 (total 77)
)
sat
(objectives
 (total 177)
)
sat
(objectives
 (total 64)
)
sat
(objectives
 (total 94)
)
sat
(objectives
 (total 49)
)
sat
(objectives
 (total 61)
)
sat
(objectives
 (total 44)
)
sat
(objectives
 (total 70)
)
sat
(objectives
 (total 127)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 30)
)
sat
(objectives
 (total 258)
)
sat
(objectives
 (total 45)
)
sat
(objectives
 (total 45)
)
sat
(objectives
 (total 20)
)
sat
(objectives
 (total 82)
)
sat
(objectives
 (total 182)
)
sat
(objectives
 (total 90)
)
sat
(objectives
 (total 155)
)
sat
(objectives
 (total 46)
)
sat
(objectives
 (total 173)
)
sat
(objectives
 (total 46)
)
sat
(objectives
 (total 169)
)
sat
(objectives
 (total 179)
)
sat
(objectives
 (total 138)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 63)
)
sat
(objectives
 (total 18)
)
sat
(objectives
 (total 51)
)
sat
(objectives
 (total 75)
)
sat
(objectives
 (total 88)
)
sat
(objectives
 (total 226)
)
sat
(objectives
 (total 76)
)
sat
(objectives
 (total 38)
)
sat
(objectives
 (total 72)
)
sat
(objectives
 (total 56)
)
sat
(objectives
 (total 204)
)
sat
(objectives
 (total 37)
)
sat
(objectives
 (total 17)
)
sat
(objectives
 (total 214)
)
sat
(objectives
 (total 90)
)
sat
(objectives
 (total 205)
)
sat
(objectives
 (total 108)
)
sat
(objectives
 (total 256)
)
sat
(objectives
 (total 65)
)
sat
(objectives
 (total 79)
)
sat
(objectives
 (total 93)
)
sat
(objectives
 (total 75)
)
sat
(objectives
 (total 61)
)
sat
(objectives
 (total 34)
)
sat
(objectives
 (total 65)
)
sat
(objectives
 (total 17)
)
sat
(objectives
 (total 88)
)
sat
(objectives
 (total 104)
)
sat
(objectives
 (total 35)
)
sat
(objectives
 (total 91)
)
sat
(objectives
 (total 42)
)
sat
(objectives
 (total 211)
)
sat
(objectives
 (total 98)
)
sat
(objectives
 (total 207)
)
sat
(objectives
 (total 84)
)
sat
(objectives
 (total 159)
)
sat
(objectives
 (total 59)
)
sat
(objectives
 (total 85)
)
sat
(objectives
 (total 44)
)
sat
(objectives
 (total 214)
)
sat
(objectives
 (total 41)
)
sat
(objectives
 (total 162)
)
sat
(objectives
 (total 51)
)
sat
(objectives
 (total 34)
)
sat
(objectives
 (total 65)
)
sat
(objectives
 (total 75)
)
sat
(objectives
 (total 235)
)
sat
(objectives
 (total 102)
)
sat
(objectives
 (total 57)
)
sat
(objectives
 (total 115)
)
sat
(objectives
 (total 47)
)
sat
(objectives
 (total 281)
)
sat
(objectives
 (total 15)
)
sat
(objectives
 (total 76)
)
sat
(objectives
 (total 62)
)
sat
(objectives
 (total 61)
)
sat
(objectives
 (total 84)
)
sat
(objectives
 (total 207)
)
sat
(objectives
 (total 272)
)
sat
(objectives
 (total 57)
)
sat
(objectives
 (total 221)
)
sat
(objectives
 (total 114)
)
sat
(objectives
 (total 50)
)
sat
(objectives
 (total 134)
)
sat
(objectives
 (total 125)
)
sat
(objectives
 (total 67)
)
sat
(objectives
 (total 241)
)
sat
(objectives
 (total 125)
)
sat
(objectives
 (total 26)
)
sat
(objectives
 (total 72)
)
sat
(objectives
 (total 63)
)
sat
(objectives
 (total 36)
)
sat
(objectives
 (total 194)
)
sat
(objectives
 (total 34)
)
sat
(objectives
 (total 91)
)
sat
(objectives
 (total 245)
)
sat
(objectives
 (total 68)
)
sat
(objectives
 (total 218)
)
sat
(objectives
 (total 58)
)
sat
(objectives
 (total 246)
)
sat
(objectives
 (total 258)
)
sat
(objectives
 (total 71)
)
sat
(objectives
 (total 162)
)
sat
(objectives
 (total 136)
)
sat
(objectives
 (total 17)
)
sat
(objectives
 (total 34)
)
sat
(objectives
 (total 128)
)
"""
