package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen
import de.devdudes.aoc.helpers.transpose

class Day13 : Day(
    description = 13 - "Point of Incidence",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Summarizing Notes",
            input = "day13",
            testInput = "day13_test",
            expectedTestResult = 405,
            solutionResult = 34_911,
            solution = { input ->
                parseMirrorValley(input)
                    .calculateReflectionScore()
            }
        )

        puzzle(
            description = 2 - "Fix Smudge",
            input = "day13",
            testInput = "day13_test",
            expectedTestResult = 400,
            solutionResult = 33_183,
            solution = { input ->
                parseMirrorValley(input)
                    .fixSmudgeAndCalculateReflectionScore()
            }
        )
    }
)

private fun parseMirrorValley(input: List<String>): MirrorValley =
    input.splitWhen { it.isEmpty() }
        .map { patternLines ->
            patternLines.map { line -> line.toCharArray().toList() }
                .let(::MirrorPattern)
        }.let(::MirrorValley)

private data class MirrorValley(val patterns: List<MirrorPattern>) {

    fun calculateReflectionScore(): Int =
        patterns.sumOf { pattern ->
            pattern.calculateReflectionScore()
        }

    fun fixSmudgeAndCalculateReflectionScore(): Int =
        patterns.sumOf { pattern ->
            pattern.fixSmudgeAndCalculateReflectionScore()
        }
}

private data class MirrorPattern(val grid: List<List<Char>>) {

    fun calculateReflectionScore(): Int {
        val rowIndices = findReflectionIndices(grid.transpose())
        return if (rowIndices.isNotEmpty()) {
            rowIndices.first() * 100
        } else {
            findReflectionIndices(grid).first()
        }
    }

    fun fixSmudgeAndCalculateReflectionScore(): Int {
        val rowIndices = findReflectionIndices(grid.transpose())
        val columnIndices = findReflectionIndices(grid)

        // iterate over the whole grid and change one element at a time
        // with the new grid try to find a new reflection axis (one that is not contained in rowIndices or columnIndices)
        for (y in grid.indices) {
            for (x in grid.first().indices) {
                // create new grid wich changes element
                val changedGrid = grid.toMutableList().also { rows ->
                    rows[y] = rows[y].toMutableList().also { row ->
                        row[x] = if (row[x] == '.') '#' else '.'
                    }
                }

                // find all reflection axis and remove existing ones
                val rowIndicesOfChangedGrid = findReflectionIndices(changedGrid.transpose()) - rowIndices
                val columnIndicesOfChangedGrid = findReflectionIndices(changedGrid) - columnIndices

                // if a new axis was found return the reflection score
                if (rowIndicesOfChangedGrid.isNotEmpty()) {
                    return rowIndicesOfChangedGrid.first() * 100
                } else if (columnIndicesOfChangedGrid.isNotEmpty()) {
                    return columnIndicesOfChangedGrid.first()
                }
            }
        }

        throw Exception("Some valid result must be found by this method")
    }

    private fun findReflectionIndices(elements: List<List<Char>>): Set<Int> {
        val indicesPerRow = elements.map { row -> findReflectionIndices(row) }
        val indices = indicesPerRow.flatten().toSet()
        return indices.mapNotNull { i ->
            if (indicesPerRow.all { it.contains(i) }) i else null
        }.toSet()
    }

    private fun findReflectionIndices(elements: List<Char>): List<Int> {
        val indices = mutableListOf<Int>()

        // for every position in the list check if the sub lists are mirrored
        for (i in 1 until elements.size) {
            // the item count which exists in both sections
            val itemCount = minOf(i, elements.size - i)

            // the start offset defines how many items to ignore from the beginning (items that are not in the first section)
            val startOffset = i - itemCount

            // obtain elements
            val leftSection = elements.drop(startOffset).take(itemCount)
            val rightSection = elements.drop(startOffset + itemCount).take(itemCount)
            if (leftSection == rightSection.reversed()) {
                indices.add(i)
            }
        }
        return indices
    }

}
