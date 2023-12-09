package de.devdudes.aoc.aoc2020


import de.devdudes.aoc.aoc2020.days.Day01
import de.devdudes.aoc.aoc2020.days.Day02
import de.devdudes.aoc.aoc2020.days.Day03
import de.devdudes.aoc.aoc2020.days.Day04
import de.devdudes.aoc.aoc2020.days.Day05
import de.devdudes.aoc.aoc2020.days.Day06
import de.devdudes.aoc.aoc2020.days.Day07
import de.devdudes.aoc.aoc2020.days.Day08
import de.devdudes.aoc.aoc2020.days.Day09
import de.devdudes.aoc.aoc2020.days.Day10
import de.devdudes.aoc.aoc2020.days.Day11
import de.devdudes.aoc.aoc2020.days.Day12
import de.devdudes.aoc.aoc2020.days.Day13
import de.devdudes.aoc.aoc2020.days.Day14
import de.devdudes.aoc.aoc2020.days.Day15
import de.devdudes.aoc.aoc2020.days.Day16
import de.devdudes.aoc.aoc2020.days.Day17
import de.devdudes.aoc.aoc2020.days.Day18
import de.devdudes.aoc.aoc2020.days.Day19
import de.devdudes.aoc.aoc2020.days.Day20
import de.devdudes.aoc.aoc2020.days.Day21
import de.devdudes.aoc.aoc2020.days.Day22
import de.devdudes.aoc.aoc2020.days.Day23
import de.devdudes.aoc.aoc2020.days.Day24
import de.devdudes.aoc.aoc2020.days.Day25
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.Year

/**
 * Event: https://adventofcode.com/2020
 */
class AdventOfCode2020 : Year(resourceFolder = "2020") {

    override val days: List<Day>
        get() = listOf(
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
