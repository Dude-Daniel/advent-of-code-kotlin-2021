package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Line
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.lineTo
import de.devdudes.aoc.helpers.move
import kotlin.math.abs

class Day01 : Day(
    description = 1 - "No Time for a Taxicab",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Total Distance to Easter Bunny HQ",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 8,
            solutionResult = 287,
            solution = { input ->
                HeadquarterStreetGrid(input.first())
                    .findDistanceToEnd()
            }
        )

        puzzle(
            description = 2 - "Distance to first already visited Position",
            input = "day01",
            testInput = "day01_test",
            expectedTestResult = 4,
            solutionResult = 133,
            solution = { input ->
                HeadquarterStreetGrid(input.first())
                    .findDistanceToFirstAlreadyVisitedLocation()
            }
        )
    }
)

private class HeadquarterStreetGrid(private val input: String) {

    private fun parseDirections() = input.split(", ")
        .map { direction ->
            val distance = direction.drop(1).toInt()
            when (direction.take(1)) {
                "L" -> MoveDirection.Left(distance)
                "R" -> MoveDirection.Right(distance)
                else -> throw Exception("Invalid Direction")
            }
        }

    fun findDistanceToEnd(): Int {
        val directions = parseDirections()

        var currentDirection = Direction.TOP
        var xDelta = 0
        var yDelta = 0

        directions.forEach { move ->
            currentDirection = when (move) {
                is MoveDirection.Left -> currentDirection.turnLeft()
                is MoveDirection.Right -> currentDirection.turnRight()
            }

            when (currentDirection) {
                Direction.TOP -> xDelta += move.distance
                Direction.BOTTOM -> xDelta -= move.distance
                Direction.LEFT -> yDelta -= move.distance
                Direction.RIGHT -> yDelta += move.distance
            }
        }

        return abs(xDelta) + abs(yDelta)
    }

    fun findDistanceToFirstAlreadyVisitedLocation(): Int {
        val directions = parseDirections()

        var currentDirection = Direction.TOP
        val path = mutableListOf<Line>()

        directions.forEach { move ->
            currentDirection = when (move) {
                is MoveDirection.Left -> currentDirection.turnLeft()
                is MoveDirection.Right -> currentDirection.turnRight()
            }

            val lastPoint = path.lastOrNull()?.to ?: Point(0, 0)
            val nextPoint = lastPoint.move(currentDirection, move.distance)
            val line = lastPoint lineTo nextPoint

            val intersection = path.firstNotNullOfOrNull {
                it.calculateIntersectionPoint(line, excludeEnds = true)
            }

            if (intersection != null) {
                println("RES: $intersection")
                return abs(intersection.x) + abs(intersection.y)
            } else {
                path.add(line)
            }
        }

        throw Exception("No point was visited twice")
    }
}

private sealed class MoveDirection {
    abstract val distance: Int

    data class Left(override val distance: Int) : MoveDirection()
    data class Right(override val distance: Int) : MoveDirection()
}
