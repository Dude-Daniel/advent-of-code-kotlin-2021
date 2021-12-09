package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day09 : Day(description = 9 - "Smoke Basin", {

    fun List<String>.parseInput(): List<List<Int>> {
        return map { row -> row.toList().map { it.toString().toInt() } }
    }

    puzzle(
        description = 1 - "Low Points",
        input = "day09",
        testInput = "day09_test",
        expectedTestResult = 15,
        solutionResult = 560,
        solution = { input ->
            val lowPoints = SmokeBasinLandscape(input.parseInput()).findLowPoints()
            lowPoints.sum() + lowPoints.size
        }
    )

    puzzle(
        description = 2 - "Three Largest Basins",
        input = "day09",
        testInput = "day09_test",
        expectedTestResult = 1134,
        solutionResult = 959136,
        solution = { input ->
            val basins = SmokeBasinFinder(input.parseInput()).findBasins()
            val largestBasins = basins.sortedByDescending { it.size }.take(3)
            println("largest: $largestBasins")
            largestBasins.fold(1) { acc, basin -> acc * basin.size }
        }
    )
})

private class SmokeBasinLandscape(private val data: List<List<Int>>) {
    fun findLowPoints(): List<Int> {
        val lowPoints = mutableListOf<Int>()
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, value ->
                val neighbors = data.findDirectNeighbors(x, y)
                val lowerNeighbor = neighbors.firstOrNull { it <= value }
                if (lowerNeighbor == null) lowPoints.add(value)
            }
        }
        return lowPoints
    }
}

private data class SmokeBasin(val basinId: Int, val size: Int)
private data class SmokeBasinFinderEntry(val value: Int, val basinId: Int?)
private class SmokeBasinFinder(private val data: List<List<Int>>) {

    fun findBasins(): List<SmokeBasin> {
        val solution = data.map { row ->
            row.map { SmokeBasinFinderEntry(value = it, basinId = null) }.toMutableList()
        }.toMutableList()

        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, value ->
                val basinId = data.size * y + x
                solution.markAdjacent(x = x, y = y, basinId = basinId)
            }
        }

        return solution.flatten()
            .filter { it.basinId != null && it.basinId >= 0 }
            .groupBy { it.basinId }
            .values.map { SmokeBasin(it.first().basinId!!, it.size) }
    }

    private fun MutableList<MutableList<SmokeBasinFinderEntry>>.markAdjacent(x: Int, y: Int, basinId: Int) {
        val entry = this.getOrNull(y)?.getOrNull(x)

        if (entry == null || entry.basinId != null) return // out of bounds OR already visited

        if (entry.value == 9) {
            this[y][x] = this[y][x].copy(basinId = -1)
        } else {
            this[y][x] = this[y][x].copy(basinId = basinId)

            markAdjacent(x = x + 1, y = y, basinId = basinId)
            markAdjacent(x = x - 1, y = y, basinId = basinId)
            markAdjacent(x = x, y = y + 1, basinId = basinId)
            markAdjacent(x = x, y = y - 1, basinId = basinId)
        }
    }
}

private fun <T : Any> List<List<T>>.findDirectNeighbors(x: Int, y: Int): List<T> = listOfNotNull(
    getOrNull(y - 1)?.getOrNull(x),
    getOrNull(y + 1)?.getOrNull(x),
    getOrNull(y)?.getOrNull(x - 1),
    getOrNull(y)?.getOrNull(x + 1)
)
