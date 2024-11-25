package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.transpose

class Day03 : Day(
    description = 3 - "Squares With Three Sides",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "One Triangle per Row",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 2,
            solutionResult = 982,
            solution = { input ->
                parseTrianglesPerRow(input).countValidTriangles()
            }
        )

        puzzle(
            description = 2 - "Vertical Triangles across Rows",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 2,
            solutionResult = 1826,
            solution = { input ->
                parseTrianglesVerticallyAcrossRows(input).countValidTriangles()

            }
        )
    }
)

private fun parseTrianglesPerRow(input: List<String>): List<Triangle> =
    input.map { triangleString ->
        val (a, b, c) = triangleString.split(" ").filter { it.isNotBlank() }
        Triangle(a.toInt(), b.toInt(), c.toInt())
    }

/**
 * 1. Parse input and convert to list of ints: [1, 2, 3]
 * 2. Transpose the input, so columns become rows -> 3 rows with a lot of elements
 * 3. Flatten the 3 rows to a single row
 * 4. Take chunks of 3 elements and consider them as one Triangle
 */
private fun parseTrianglesVerticallyAcrossRows(input: List<String>): List<Triangle> =
    input.map { triangleString ->
        val (a, b, c) = triangleString.split(" ").filter { it.isNotBlank() }
        listOf(a.toInt(), b.toInt(), c.toInt())
    }.transpose()
        .flatten()
        .chunked(3)
        .map { (a, b, c) -> Triangle(a, b, c) }

private data class Triangle(val a: Int, val b: Int, val c: Int) {
    val isValid: Boolean = a + b > c && a + c > b && b + c > a
}

private fun List<Triangle>.countValidTriangles(): Int = count { triangle -> triangle.isValid }
