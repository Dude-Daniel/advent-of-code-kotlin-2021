package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.transpose
import kotlin.math.abs

class Day01 : Day(
    description = 1 - "Historian Hysteria",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Total Distance",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 11,
            solutionResult = 1_579_939,
            solution = { input ->
                input.map { row -> row.split("   ").map(String::toInt) }
                    .transpose() // transpose so we have 2 lists
                    .map(List<Int>::sorted) // sort each list
                    .transpose() // transpose back to original representation
                    .sumOf { abs(it.first() - it.last()) } // sum of absolute differences
            }
        )

        puzzle(
            description = 2 - "Similarity Score",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 31,
            solutionResult = 20_351_745,
            solution = { input ->
                val (leftList, rightList) =
                    input.map { row -> row.split("   ").map(String::toInt) }
                        .transpose() // transpose so we have 2 lists

                val idsWithCount = leftList.groupBy { it } // group by id number
                    .mapValues { it.value.size } // count the number of occurrences
                    .toList() // convert to list of pairs (id, count)

                idsWithCount.sumOf { (id, count) ->
                    val similarityScore = id * rightList.count { rightId -> rightId == id }
                    similarityScore * count
                }
            }
        )
    }
)
