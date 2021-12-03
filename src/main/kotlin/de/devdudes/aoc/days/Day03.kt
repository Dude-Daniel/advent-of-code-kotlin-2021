package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day03 : Day(description = 3 - "Binary Diagnostic", {

    puzzle(
        description = 1 - "Power Consumption",
        input = "day03",
        testInput = "day03_test",
        expectedTestResult = 198,
        solutionResult = 738234,
        solution = { input ->
            val gammaRate = input.toDigitList()
                .reduce { sum, digits ->
                    sum.zip(digits).map {
                        if (it.second == 0) it.first else it.first + 1
                    }
                }.map { digitCount ->
                    val threshold = input.size / 2
                    if (digitCount < threshold) 0 else 1
                }

            val epsilonRate = gammaRate.map { if (it == 0) 1 else 0 }
            gammaRate.toIntFromBytes() * epsilonRate.toIntFromBytes()
        }
    )

    puzzle(
        description = 2 - "Life Support Rating",
        input = "day03",
        testInput = "day03_test",
        expectedTestResult = 230,
        solutionResult = 3969126,
        solution = { input ->
            fun List<List<Int>>.selectByBitCountAtEachPosition(
                position: Int = 0,
                predicate: (Int, Int) -> Boolean
            ): List<Int> {
                val ones = count { it[position] == 1 }
                val zeros = size - ones
                val digitToKeep = if (predicate(zeros, ones)) 1 else 0
                val filtered = filter { it[position] == digitToKeep }
                return if (filtered.size == 1 || position + 1 >= first().size) filtered.first()
                else filtered.selectByBitCountAtEachPosition(position + 1, predicate)
            }

            fun List<List<Int>>.oxygenGeneratorRating(): List<Int> =
                selectByBitCountAtEachPosition { zeros, ones -> ones >= zeros }

            fun List<List<Int>>.co2ScrubberRating(): List<Int> =
                selectByBitCountAtEachPosition { zeros, ones -> ones < zeros }

            val inputDigits = input.toDigitList()
            val oxygenDigits = inputDigits.oxygenGeneratorRating()
            val co2Digits = inputDigits.co2ScrubberRating()
            oxygenDigits.toIntFromBytes() * co2Digits.toIntFromBytes()
        }
    )
})

private fun List<String>.toDigitList(): List<List<Int>> = map { line -> line.toList().map { it.digitToInt() } }
private fun List<Int>.toIntFromBytes(): Int = fold(0) { acc: Int, digit: Int -> (acc shl 1) or digit }
