package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day03 : Day(
    description = 3 - "Gondola Lift Station - Gear Ratios",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum of any number adjacent to a symbol",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 4361,
            solutionResult = 509115,
            solution = { input ->
                EngineSchematic(input)
                    .removeNumbersNotAdjacentToAnySymbol()
                    .getNumbers()
                    .sum()
            }
        )

        puzzle(
            description = 2 - "Sum of all gear ratios",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 467835,
            solutionResult = 75220503,
            solution = { input ->
                EngineSchematic(input)
                    .getGearNumberPairs()
                    .sumOf { it.first * it.second }
            }
        )
    }
)

private data class EngineSchematic(private val schematic: List<String>) {

    fun removeNumbersNotAdjacentToAnySymbol(): EngineSchematic {
        val mutableGrid = schematic.map(::StringBuilder).toMutableList()

        getNumbersWithCoordinates().forEach { (_, position) ->
            val (xRange, y) = position
            // check if number has any adjacent symbol around it (not a number and not a '.')
            if (!schematic.hasNeighbor(xRange = xRange, y = y) { !it.isDigit() && it != '.' }) {
                // replace number by "."
                xRange.forEach { x -> mutableGrid[y][x] = '.' }
            }
        }

        return EngineSchematic(mutableGrid.map { it.toString() })
    }

    fun getGearNumberPairs(): List<Pair<Int, Int>> {
        val numbers = getNumbersWithCoordinates()

        return schematic.flatMapIndexed { rowIndex, row ->
            // obtain all indices of gears "*"
            row.mapIndexedNotNull { index, c -> if (c == '*') index else null }
                .map { columnIndex ->
                    // get all numbers which are adjacent to the gear (one position around the gears index in all directions)
                    numbers.filter { (_, position) ->
                        val (xRange, y) = position

                        val matchesRow = (rowIndex - 1..rowIndex + 1).contains(y)
                        val matchesColumn = (columnIndex - 1..columnIndex + 1)
                            .intersect(xRange)
                            .isNotEmpty()

                        matchesRow && matchesColumn
                    }
                }.filter { it.size == 2 } // make sure every gear exactly has two adjacent numbers
                .map { (numberOne, numberTwo) -> Pair(numberOne.first, numberTwo.first) }
        }
    }

    fun getNumbers(): List<Int> =
        schematic.flatMap { row -> Regex("\\d+").findAll(row).map { it.value.toInt() } }

    /**
     * Extracts the numbers from the given [schematic] and returns the number with their position.
     * As the number might be larger than one digit the x position is defined as [IntRange].
     */
    private fun getNumbersWithCoordinates(): List<Pair<Int, Pair<IntRange, Int>>> =
        schematic.flatMapIndexed { y, row ->
            Regex("\\d+").findAll(row).map { match ->
                Pair(match.value.toInt(), Pair(match.range, y))
            }
        }
}

private fun List<String>.hasNeighbor(
    xRange: IntRange,
    y: Int,
    predicate: (Char) -> Boolean,
): Boolean {
    for (posY in y - 1..y + 1) {
        for (posX in xRange.first - 1..xRange.last + 1) {
            val neighbor = getOrNull(posY)?.getOrNull(posX)
            if (neighbor != null && predicate(neighbor)) {
                return true
            }
        }
    }
    return false
}
