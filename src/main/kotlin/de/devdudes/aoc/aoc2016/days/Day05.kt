package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.md5

class Day05 : Day(
    description = 5 - "How About a Nice Game of Chess?",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Password Chars from Hash in Order",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = "18f47a30",
            solutionResult = "d4cd2ee1",
            solution = { input ->
                PasswordGenerator(
                    doorId = input.first(),
                    startIndex = if (isTest) 3231929 else 702868,
                ).generateInOrder()
            }
        )

        puzzle(
            description = 2 - "Password Chars and Positions from Hash",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = "05ace8e3",
            solutionResult = "f2c730e5",
            solution = { input ->
                PasswordGenerator(
                    doorId = input.first(),
                    startIndex = if (isTest) 3231929 else 1776010,
                ).generateWithPosition()
            }
        )
    }
)

private class PasswordGenerator(
    private val doorId: String,
    private val startIndex: Int = 0,
) {

    fun generateInOrder(): String {
        var password = ""
        var index = startIndex
        while (password.length < 8) {
            val hash = (doorId + index.toString()).md5()
            if (hash.startsWith("00000")) {
                println("Index: $index - Hash: $hash -> Password: $password")
                password += hash[5]
            }
            index++
        }
        return password
    }

    fun generateWithPosition(): String {
        val password = mutableListOf<Char?>(null, null, null, null, null, null, null, null)
        var charsToObtain = password.size
        var index = startIndex
        while (charsToObtain > 0) {
            val hash = (doorId + index.toString()).md5()
            val position = hash[5].toString().toIntOrNull()
            val char = hash[6]
            if (position != null && position <= 7 && hash.startsWith("00000")) {
                if (password[position] == null) {
                    password[position] = char
                    charsToObtain -= 1
                }
                println("Index: $index - Hash: $hash -> Password: $password")
            }
            index++
        }
        return password.joinToString("")
    }
}
