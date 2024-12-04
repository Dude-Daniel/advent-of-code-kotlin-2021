package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.contains
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.moveBottomLeft
import de.devdudes.aoc.helpers.moveBottomRight
import de.devdudes.aoc.helpers.moveTopLeft
import de.devdudes.aoc.helpers.moveTopRight
import de.devdudes.aoc.helpers.transpose

class Day04 : Day(
    description = 4 - "Ceres Search",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "XMAS occurrences in an Direction",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 18,
            solutionResult = 2_599,
            solution = { input ->
                WordSearch(input).findXmasTextCount()
            }
        )

        puzzle(
            description = 2 - "X-MAS occurrences in X Shape",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 9,
            solutionResult = 1_948,
            solution = { input ->
                WordSearch(input).findMasInXShapeCount()
            }
        )
    }
)

private class WordSearch(private val input: List<String>) {

    private fun parseInput(): Grid2D<Char> = Grid2D(input.map { it.toCharArray().toList() })

    fun findXmasTextCount(): Int {
        val text = "XMAS"
        val charGrid = parseInput()

        var counter = charGrid.countHorizontalMatches(text) +
                charGrid.transpose().countHorizontalMatches(text)

        charGrid.forEachIndexed { point: Point, _: Char ->
            counter += charGrid.countDiagonalMatches(point, text)
        }

        return counter
    }

    private fun Grid2D<Char>.countHorizontalMatches(text: String): Int {
        val textForward = text.toCharArray().toList()
        val textReversed = textForward.reversed()

        return getRawValues().sumOf { row ->
            row.windowed(text.length, 1)
                .count { elements ->
                    elements == textForward || elements == textReversed
                }
        }
    }

    private fun Grid2D<Char>.countDiagonalMatches(point: Point, text: String): Int {
        var counter = 0
        val chars = text.toCharArray().toList()

        if (this.contains(values = chars, pointAt = { index -> point.moveTopLeft(distance = index) })) counter++
        if (this.contains(values = chars, pointAt = { index -> point.moveTopRight(distance = index) })) counter++
        if (this.contains(values = chars, pointAt = { index -> point.moveBottomLeft(distance = index) })) counter++
        if (this.contains(values = chars, pointAt = { index -> point.moveBottomRight(distance = index) })) counter++

        return counter
    }

    fun findMasInXShapeCount(): Int {
        val text = "MAS"
        val chars = text.toCharArray().toList()

        val charGrid = parseInput()

        var counter = 0
        charGrid.forEachIndexed { point: Point, _: Char ->
            // Top Left to Bottom Right
            val matchesTLBR = charGrid.contains(values = chars, pointAt = { index -> point.moveTopLeft().moveBottomRight(distance = index) })
            // Bottom Right to Top Left
            val matchesBRTL = charGrid.contains(values = chars, pointAt = { index -> point.moveBottomRight().moveTopLeft(distance = index) })

            // Top Right to Bottom Left
            val matchesTRBL = charGrid.contains(values = chars, pointAt = { index -> point.moveTopRight().moveBottomLeft(distance = index) })
            // Bottom Left to Top Right
            val matchesBLTR = charGrid.contains(values = chars, pointAt = { index -> point.moveBottomLeft().moveTopRight(distance = index) })

            if ((matchesTLBR || matchesBRTL) && (matchesTRBL || matchesBLTR)) counter += 1
        }

        return counter
    }
}
