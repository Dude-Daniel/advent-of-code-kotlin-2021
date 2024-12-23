package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day22 : Day(
    description = 22 - "Monkey Market",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Unknown",
            input = "day22",
            testInput = "day22_test",
            expectedTestResult = 37_327_623L,
            solutionResult = 16_619_522_798L,
            solution = { input ->
                parseSecretNumbers(input)
                    .sumOf { secretNumber ->
                        secretNumber.next(2000).value
                    }
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day22",
            testInput = "day22_test",
            expectedTestResult = Unit,
            solutionResult = Unit,
            solution = { input ->
                // not implemented yet
                TODO()
            }
        )
    }
)

/*
123:

15887950
16495136
527345
704524
1553684
12683156
11100544
12249484
7753432
5908254
 */

private fun parseSecretNumbers(input: List<String>): List<SecretNumber> =
    input.map { SecretNumber(it.toLong()) }

private data class SecretNumber(val value: Long) {

    fun next(steps: Int): SecretNumber {
        var result = this
        repeat(steps) {
            result = result.next()
//            println(result.value.toString())
        }
        return result
    }

    fun next(): SecretNumber {
        var result = mixAndPrune(value * 64, value)
        result = mixAndPrune(result / 32, result)
        result = mixAndPrune(result * 2048, result)
        return SecretNumber(result)
    }

    companion object {
        private fun mixAndPrune(value: Long, secret: Long): Long {
            val mixedValue = value xor secret
            return mixedValue.mod(16777216L)
        }
    }
}
