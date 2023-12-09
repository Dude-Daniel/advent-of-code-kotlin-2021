package de.devdudes.aoc.aoc2015.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day02 : Day(
    description = 2 - "I Was Told There Would Be No Math",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Total area of Wrapping Paper",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 101,
            solutionResult = 1_588_178,
            solution = { input ->
                parsePresentWrapper(input)
                    .calculatePaperArea()
            }
        )

        puzzle(
            description = 2 - "Total length of Ribbon",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 48,
            solutionResult = 3_783_758,
            solution = { input ->
                parsePresentWrapper(input)
                    .calculateRibbonLength()
            }
        )
    }
)

private fun parsePresentWrapper(input: List<String>): PresentWrapper {
    return input.map { line ->
        val (l, w, h) = line.split("x").map(String::toInt)
        PresentDimensions(l, w, h)
    }.let(::PresentWrapper)
}

private data class PresentWrapper(val presents: List<PresentDimensions>) {

    fun calculatePaperArea(): Int = presents.sumOf { it.calculatePaperArea() }

    fun calculateRibbonLength(): Int = presents.sumOf { it.calculateRibbonLength() }
}

private data class PresentDimensions(
    val length: Int,
    val width: Int,
    val height: Int,
) {

    fun calculatePaperArea(): Int {
        val areaA = length * width
        val areaB = width * height
        val areaC = height * length
        return minOf(areaA, areaB, areaC) + 2 * areaA + 2 * areaB + 2 * areaC
    }

    fun calculateRibbonLength(): Int {
        // get the two shortest sides
        val (sideA, sideB) = listOf(length, width, height).sorted()

        // wrap ribbon around the box
        val wrapDistance = 2 * sideA + 2 * sideB

        // bind a bow (Don't ask how they tie the bow, though; they'll never tell.)
        val bowLength = length * width * height

        return wrapDistance + bowLength
    }
}
