package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.findShortestPath
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.background
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.logging.greyscale
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.printIndexed
import de.devdudes.aoc.helpers.toGrid

class Day17 : Day(
    description = 17 - "Clumsy Crucible - least Heat Loss",
    ignored = false,
    days = {

        val printOutput = false

        puzzle(
            description = 1 - "Move max 3 steps in same direction",
            input = "day17",
            testInput = "day17_test",
            expectedTestResult = 102,
            solutionResult = 755,
            solution = { input ->
                val cityMap = parseCityMap(input)
                val route = cityMap.getShortestRouteMaxThreeSteps()

                if (printOutput) cityMap.printWithPath(route.points)

                route.totalHeatLoss
            }
        )

        puzzle(
            description = 2 - "Move min 4 and max 10 steps in same direction",
            input = "day17",
            testInput = "day17_test",
            expectedTestResult = 94,
            solutionResult = 881,
            solution = { input ->
                val cityMap = parseCityMap(input)
                val route = cityMap.getShortestRouteMinFourMaxTenSteps()

                if (printOutput) cityMap.printWithPath(route.points)

                route.totalHeatLoss
            }
        )
    }
)

private fun parseCityMap(input: List<String>): CityMap =
    input.map { row ->
        row.map { it.digitToInt() }
    }.toGrid().let(::CityMap)

private data class CityPointData(
    val point: Point,
    val direction: Direction,
    val directionSteps: Int,
) {
    fun move(direction: Direction): Pair<CityPointData, Point> {
        val newPoint = point.move(direction)
        return CityPointData(
            point = newPoint,
            direction = direction,
            directionSteps = if (this.direction == direction) directionSteps + 1 else 1
        ) to newPoint
    }
}

private data class CityMap(val coordinates: Grid2D<Int>) {

    fun printWithPath(pathPoints: List<Point>): CityMap = also {
        coordinates.printIndexed { point, value ->
            value.toString().background(greyscale(value, 10))
                .colored(if (point in pathPoints) LogColor.Red else LogColor.Green)
        }
    }

    fun getShortestRouteMaxThreeSteps(): CityRoute {
        val start = CityPointData(Point(0, 0), Direction.RIGHT, 0)

        val shortestPath = coordinates.findShortestPath(
            start = Point(0, 0),
            startData = start,
            end = coordinates.requireLastPoint,
            neighbours = { data, _ ->
                data.direction.possibleDirections(
                    canMoveStraight = data.directionSteps < 3,
                    canTurn = true,
                ).map { direction -> data.move(direction) }
            },
            cost = { _, position -> coordinates[position] },
        )

        return CityRoute(
            points = shortestPath.path.map { it.point },
            totalHeatLoss = shortestPath.score,
        )
    }

    fun getShortestRouteMinFourMaxTenSteps(): CityRoute {
        val start = CityPointData(Point(0, 0), Direction.RIGHT, 0)

        val shortestPath = coordinates.findShortestPath(
            start = Point(0, 0),
            startData = start,
            end = coordinates.requireLastPoint,
            endCondition = { data, _ -> data.directionSteps >= 4 },
            neighbours = { data, _ ->
                data.direction.possibleDirections(
                    canMoveStraight = data.directionSteps < 10,
                    canTurn = data.directionSteps >= 4 || data.directionSteps == 0
                ).map { direction -> data.move(direction) }
            },
            cost = { _, position -> coordinates[position] },
        )

        return CityRoute(
            points = shortestPath.path.map { it.point },
            totalHeatLoss = shortestPath.score,
        )
    }
}

private data class CityRoute(val points: List<Point>, val totalHeatLoss: Int)

private fun Direction.possibleDirections(canMoveStraight: Boolean, canTurn: Boolean): Set<Direction> {
    val changingDirections =
        if (isHorizontal) setOf(Direction.TOP, Direction.BOTTOM)
        else setOf(Direction.LEFT, Direction.RIGHT)

    return buildSet {
        if (canTurn) addAll(changingDirections)
        if (canMoveStraight) add(this@possibleDirections)
    }
}
