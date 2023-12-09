package de.devdudes.aoc.aoc2015.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day03 : Day(
    description = 3 - "Perfectly Spherical Houses in a Vacuum - Number of Houses that receive presents",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Deliveries by Santa",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 4,
            solutionResult = 2081,
            solution = { input ->

                // start position is already visited
                val visitsOnStart = mapOf(HouseCoordinate(0, 0) to 1)

                SantasHouseGrid.deliverPresents(
                    directions = input.first().toList(),
                    visitedPositions = visitsOnStart,
                ).size
            }
        )

        puzzle(
            description = 2 - "Deliveries by Santa and Robo-Santa",
            input = "day03",
            testInput = "day03_test",
            expectedTestResult = 3,
            solutionResult = 2341,
            solution = { input ->
                val allDirections = input.first().toList().withIndex()

                // start position is already visited
                val visitsOnStart = mapOf(HouseCoordinate(0, 0) to 1)

                // let santa move around with every direction on an even index
                val santaSteps = allDirections.filter { it.index % 2 == 0 }.map { it.value }
                val visitedBySanta = SantasHouseGrid.deliverPresents(
                    directions = santaSteps,
                    visitedPositions = visitsOnStart,
                )

                // let robo santa move around with every direction on an odd index
                val roboSantaSteps = allDirections.filter { it.index % 2 == 1 }.map { it.value }
                val visitedByRoboSanta = SantasHouseGrid.deliverPresents(
                    directions = roboSantaSteps,
                    visitedPositions = visitedBySanta,
                )

                visitedByRoboSanta.size
            }
        )
    }
)

private object SantasHouseGrid {

    fun deliverPresents(
        directions: List<Char>,
        visitedPositions: Map<HouseCoordinate, Int>,
    ): MutableMap<HouseCoordinate, Int> {

        // start position is always 0,0
        val startValues = HouseCoordinate(0, 0) to visitedPositions.toMutableMap()

        return directions.fold(startValues) { (currentPosition, visits), direction: Char ->
            // move to the next position
            val nextPosition = currentPosition.move(direction)

            // increase the visit count of the position
            val visitCount = visits.getOrDefault(nextPosition, 0)
            visits[nextPosition] = visitCount + 1

            // pass new position and visit counts to next step
            nextPosition to visits
        }.second
    }
}

private data class HouseCoordinate(val x: Int, val y: Int) {

    fun move(direction: Char): HouseCoordinate =
        when (direction) {
            '^' -> HouseCoordinate(x, y + 1)
            'v' -> HouseCoordinate(x, y - 1)
            '>' -> HouseCoordinate(x + 1, y)
            else -> HouseCoordinate(x - 1, y)
        }
}
