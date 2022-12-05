package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.lang.UnsupportedOperationException

class Day03 : Day(
    description = 3 - "Rucksack Reorganization",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "sum of the priorities of duplicate items per backpack",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 157,
            solutionResult = 7691,
            solution = { input ->
                val priorities = input.map { backpack ->
                    val compartmentSize = backpack.length / 2
                    val (c1, c2) = backpack.chunked(compartmentSize)
                    c1.findAnyOf(c2.chunked(1))!!.second.priority
                }
                priorities.sum()
            }
        )

        puzzle(
            description = 2 - "sum of the priorities of elf group badges",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 70,
            solutionResult = 2508,
            solution = { input ->
                val groups = input.chunked(3)
                val groupPriorities = groups
                    .map { backpacks ->
                        backpacks.map { it.toCharArray().toSet() }
                    }
                    .map { (b1, b2, b3) ->
                        b1.intersect(b2).intersect(b3).first().toString().priority
                    }
                groupPriorities.sum()
            }
        )
    }
)

private val String.priority: Int
    get() = when (val char = this.toCharArray().first()) {
        in 'a'..'z' -> char.code - 96
        in 'A'..'Z' -> char.code - 38
        else -> throw IllegalArgumentException("char is not a priority: $char")
    }
