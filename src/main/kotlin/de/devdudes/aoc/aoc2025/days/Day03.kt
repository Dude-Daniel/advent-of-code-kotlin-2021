package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.concat

class Day03 : Day(
    description = 3 - "Lobby",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Maximum joltage using 2 Batteries",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 357L,
            solutionResult = 17_430L,
            solution = { input ->
                BatterySolver(input).findMaxJoltage(batteryCount = 2)
            }
        )

        puzzle(
            description = 2 - "Maximum joltage using 12 Batteries",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 3_121_910_778_619L,
            solutionResult = 171_975_854_269_367L,
            solution = { input ->
                BatterySolver(input).findMaxJoltage(batteryCount = 12)
            }
        )
    }
)

private class BatterySolver(input: List<String>) {

    private val batteryBanks: List<BatteryBank> = input.map { BatteryBank(it.toCharArray().map(Char::digitToInt)) }

    fun findMaxJoltage(batteryCount: Int): Long = batteryBanks.sumOf { it.maxJoltage(batteryCount) }

    private data class BatteryBank(val joltages: List<Int>) {

        fun maxJoltage(batteryCount: Int): Long = findNextBatteryJoltage(joltages, batteryCount)

        private fun findNextBatteryJoltage(batteries: List<Int>, batteriesToUse: Int): Long {
            // find max value if the given battery input. Ignore the last batteries so they can be picked up in the next recursive iteration.
            val maxBatteryValue = batteries.dropLast(batteriesToUse - 1).max()

            val index = batteries.indexOf(maxBatteryValue)

            return if (batteriesToUse <= 1) {
                maxBatteryValue.toLong()
            } else {
                // find the next part of the joltage value by recursively evaluating the remaining joltages (all batteries after the found battery)
                val maxSecondValue = findNextBatteryJoltage(
                    batteries = batteries.drop(index + 1),
                    batteriesToUse = batteriesToUse - 1,
                )

                maxBatteryValue.toLong().concat(maxSecondValue)
            }
        }
    }
}
