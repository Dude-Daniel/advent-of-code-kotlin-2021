package de.devdudes.aoc.aoc2025

import de.devdudes.aoc.aoc2025.days.Day01
import de.devdudes.aoc.aoc2025.days.Day02
import de.devdudes.aoc.aoc2025.days.Day03
import de.devdudes.aoc.aoc2025.days.Day04
import de.devdudes.aoc.aoc2025.days.Day05
import de.devdudes.aoc.aoc2025.days.Day06
import de.devdudes.aoc.aoc2025.days.Day07
import de.devdudes.aoc.aoc2025.days.Day08
import de.devdudes.aoc.aoc2025.days.Day09
import de.devdudes.aoc.aoc2025.days.Day10
import de.devdudes.aoc.aoc2025.days.Day11
import de.devdudes.aoc.aoc2025.days.Day12
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.Year

/**
 * Event: https://adventofcode.com/2025
 */
class AdventOfCode2025 : Year(resourceFolder = "2025") {

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
        )
}
