package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day11 : Day(description = 11 - "Dumbo Octopus", {

    puzzle(
        description = 1 - "Flashes after 100 steps",
        input = "day11",
        testInput = "day11_test",
        expectedTestResult = 1656,
        solutionResult = 1571,
        solution = { input ->
            val gridInput = input.map { line -> line.toList().map { it.toString().toInt() } }
            OctopusGrid(gridInput).simulateSteps(100)
        }
    )

    puzzle(
        description = 2 - "Step when all flash at once",
        input = "day11",
        testInput = "day11_test",
        expectedTestResult = 195,
        solutionResult = 387,
        solution = { input ->
            val gridInput = input.map { line -> line.toList().map { it.toString().toInt() } }
            val octopusGrid = OctopusGrid(gridInput)

            var flashCount = 0
            var totalSteps = 0
            while (flashCount != octopusGrid.octopusCount) {
                totalSteps += 1
                flashCount = octopusGrid.simulateSteps(1)
            }
            totalSteps
        }
    )
})

private class OctopusGrid(startGrid: List<List<Int>>) {

    private var grid: List<List<Int>> = startGrid

    val octopusCount = grid.size * grid.first().size

    /**
     * Simulates one step.
     *
     * @return the number of flashes occurred during the simulation.
     */
    fun simulateSteps(steps: Int): Int {
        var flashCount = 0
        printGrid("start")

        repeat(steps) {
            grid = grid.simulateStep()
            printGrid("after step $it")
            flashCount += grid.sumOf { row -> row.sumOf { if (it == 0) 1 else 0 as Int } }
        }

        printGrid("end")

        return flashCount
    }

    private fun List<List<Int>>.simulateStep(): List<List<Int>> {
        val increasedGrid = this.map { line -> line.map { it + 1 }.toMutableList() }.toMutableList()

        val maxX = increasedGrid.size
        val maxY = increasedGrid.first().size

        fun flashRecursively(x: Int, y: Int) {
            val value = increasedGrid.getOrNull(x)?.getOrNull(y) ?: return // out of bounds
            if (value == 0) return // already flashed

            val newValue = value + 1
            increasedGrid[x][y] = newValue

            if (newValue > 9) {
                // flash it
                increasedGrid[x][y] = 0
                flashRecursively(x - 1, y - 1)
                flashRecursively(x, y - 1)
                flashRecursively(x + 1, y - 1)
                flashRecursively(x - 1, y)
                flashRecursively(x + 1, y)
                flashRecursively(x - 1, y + 1)
                flashRecursively(x, y + 1)
                flashRecursively(x + 1, y + 1)
            }
        }

        for (x in 0 until maxX) {
            for (y in 0 until maxY) {
                if (increasedGrid[x][y] > 9) flashRecursively(x, y)
            }
        }

        return increasedGrid
    }

    private fun printGrid(text: String) {
//        println("Grid: $text")
        grid.forEach { row ->
            val rowOutput = row.joinToString(separator = "")
//            println(rowOutput)
        }
    }
}
