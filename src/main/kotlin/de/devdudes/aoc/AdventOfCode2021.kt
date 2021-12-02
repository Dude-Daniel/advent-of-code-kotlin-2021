package de.devdudes.aoc

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.days.Day01
import de.devdudes.aoc.days.Day02

fun main() {
    AdventOfCode2021().solveAllDays()
}

/**
 * Event: https://adventofcode.com/
 * Solutions: https://github.com/Bogdanp/awesome-advent-of-code#kotlin
 */
class AdventOfCode2021 {

    fun solveAllDays() {
        DAYS.forEach { day ->
            day.solve()
        }
    }

    companion object {
        private val DAYS: List<Day> = listOf(
            Day01(),
            Day02(),
        )
    }
}
