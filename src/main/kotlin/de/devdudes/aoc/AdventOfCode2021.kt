package de.devdudes.aoc

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.days.Day01
import de.devdudes.aoc.days.Day02
import de.devdudes.aoc.days.Day03
import de.devdudes.aoc.days.Day04
import de.devdudes.aoc.days.Day05
import de.devdudes.aoc.days.Day06
import de.devdudes.aoc.days.Day07
import de.devdudes.aoc.days.Day08
import de.devdudes.aoc.days.Day09
import de.devdudes.aoc.days.Day10
import de.devdudes.aoc.days.Day11
import de.devdudes.aoc.days.Day12
import de.devdudes.aoc.days.Day13
import de.devdudes.aoc.days.Day14
import de.devdudes.aoc.days.Day15
import de.devdudes.aoc.days.Day16
import de.devdudes.aoc.days.Day17
import de.devdudes.aoc.days.Day18
import de.devdudes.aoc.days.Day19
import de.devdudes.aoc.days.Day20
import de.devdudes.aoc.days.Day21
import de.devdudes.aoc.days.Day22
import de.devdudes.aoc.days.Day23
import de.devdudes.aoc.days.Day24
import de.devdudes.aoc.days.Day25

/**
 * Object for defining the main function which solves all days which are implemented (days that are not ignored).
 */
object SolveAllDays {
    @JvmStatic
    fun main(args: Array<String>) {
        AdventOfCode2021().solveAllDays()
    }
}

/**
 * Object for defining the main function which solves only the last implemented day (the day that is not ignored).
 */
object SolveLastImplementedDay {
    @JvmStatic
    fun main(args: Array<String>) {
        AdventOfCode2021().solveLastImplementedDay()
    }
}

/**
 * Event: https://adventofcode.com/
 * Solutions: https://github.com/Bogdanp/awesome-advent-of-code#kotlin
 */
class AdventOfCode2021 {

    fun solveLastImplementedDay() {
        DAYS.last { !it.ignored }.solve()

        printIgnoredDays()
    }

    fun solveAllDays() {
        DAYS.forEach { day -> day.solve() }

        printIgnoredDays()
    }

    private fun printIgnoredDays() {
        val ignoredDays = DAYS.filter { it.ignored }.map { it.javaClass.simpleName }
        if (ignoredDays.isNotEmpty()) {
            println()
            println("Ignored days (not implemented yet): $ignoredDays")
        }
    }

    companion object {
        private val DAYS: List<Day> = listOf(
            Day01(),
            Day02(),
            Day03(),
            Day04(),
            Day05(),
            Day06(),
            Day07(),
            Day08(),
            Day09(),
            Day10(),
            Day11(),
            Day12(),
            Day13(),
            Day14(),
            Day15(),
            Day16(),
            Day17(),
            Day18(),
            Day19(),
            Day20(),
            Day21(),
            Day22(),
            Day23(),
            Day24(),
            Day25(),
        )
    }
}
