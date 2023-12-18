package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.mapValues
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.print
import de.devdudes.aoc.helpers.rangeTo
import de.devdudes.aoc.helpers.toMutableGrid
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day18 : Day(
    description = 18 - "Lavaduct Lagoon - Dig out caves",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Small Caves",
            input = "day18",
            testInput = "day18_test",
            expectedTestResult = 62L,
            solutionResult = 52_055L,
            solution = { input ->
                // first approach used a grid and some fill operations
                parseDigPlan(input).digUsingGrid()
            }
        )

        puzzle(
            description = 2 - "Huge Caves",
            input = "day18",
            testInput = "day18_test",
            expectedTestResult = BigInteger.valueOf(952_408_144_115L),
            solutionResult = BigInteger("67622758357096"),
            solution = { input ->
                // first approach was too slow so shoelace algorithm was used instead
                parseDigPlanFromHex(input).digUsingShoelace()
            }
        )
    }
)

private const val printDay18 = false

private fun parseDigPlan(input: List<String>): DigPlan {

    fun parseDirection(raw: String): Direction =
        when (raw) {
            "U" -> Direction.TOP
            "D" -> Direction.BOTTOM
            "L" -> Direction.LEFT
            "R" -> Direction.RIGHT
            else -> throw IllegalArgumentException("input is not a valid direction: $raw")
        }

    return input.map { row ->
        val (direction, count) = row.split(" ")

        DigCommand(
            direction = parseDirection(direction),
            count = count.toInt(),
        )
    }.let(::DigPlan)
}

private fun parseDigPlanFromHex(input: List<String>): DigPlan {

    fun parseDirection(raw: Char): Direction =
        when (raw) {
            '3' -> Direction.TOP
            '1' -> Direction.BOTTOM
            '2' -> Direction.LEFT
            '0' -> Direction.RIGHT
            else -> throw IllegalArgumentException("input is not a valid direction: $raw")
        }

    return input.map { row ->
        val numbers = row.split(" ").last().drop(2).dropLast(1)
        val direction = parseDirection(numbers.last())

        @OptIn(ExperimentalStdlibApi::class)
        val count = numbers.dropLast(1).hexToInt()

        DigCommand(
            direction = direction,
            count = count,
        )
    }.let(::DigPlan)
}

private data class DigPlan(val commands: List<DigCommand>) {

    fun digUsingShoelace(): BigInteger {
        val polygonPoints = commands.fold(listOf(Point(x = 0, y = 0))) { acc, digCommand ->
            val nextPoint = acc.last().move(digCommand.direction, digCommand.count)
            acc + nextPoint
        }

        // Shoelace
        var shoelaceSum = BigInteger.ZERO
        for (i in polygonPoints.indices) {
            val currentPoint = polygonPoints[i]
            val previousPoint = if (i == 0) polygonPoints.last() else polygonPoints[i - 1]
            val nextPoint = if (i == polygonPoints.lastIndex) polygonPoints.first() else polygonPoints[i + 1]
            shoelaceSum += currentPoint.x.toBigInteger() * (previousPoint.y.toBigInteger() - nextPoint.y.toBigInteger())
        }

        val shoelace: BigInteger = shoelaceSum.abs() / BigInteger.valueOf(2L)

        // Boundary points
        var boundaryPoints: BigInteger = BigInteger.ZERO
        commands.forEach { boundaryPoints += it.count.toBigInteger() }

        // Inner Points
        val innerPoints: BigInteger = shoelace - boundaryPoints / BigInteger.valueOf(2L) + BigInteger.ONE

        return boundaryPoints + innerPoints
    }

    fun digUsingGrid(): Long {
        val (columns, rows, start) = calculateDimensions()
        val grid = MutableGrid2D(columns = columns, rows = rows) { CaveTerrain.SOLID }

        var currentPosition = start

        // dig tunnel in grid by iterating over each command and moving currentPosition accordingly
        commands.forEach { command ->
            val nextPosition = when (command.direction) {
                Direction.TOP -> currentPosition.copy(y = currentPosition.y - command.count)
                Direction.BOTTOM -> currentPosition.copy(y = currentPosition.y + command.count)
                Direction.LEFT -> currentPosition.copy(x = currentPosition.x - command.count)
                Direction.RIGHT -> currentPosition.copy(x = currentPosition.x + command.count)
            }

            for (point in currentPosition..Point(nextPosition.x, nextPosition.y)) {
                grid[point] = CaveTerrain.DUG_OUT
            }

            currentPosition = nextPosition
        }

        if (printDay18) {
            println("Tunnel System:")
            grid.print { it.char.toString() }
        }

        // dig out caves
        val diggedCave = CaveDigger(grid).digOutCaves()

        if (printDay18) {
            println("Caves:")
            diggedCave.grid.print { it.char.toString() }
        }

        return diggedCave.numberOfTiles(CaveTerrain.DUG_OUT).toLong()
    }

    private fun calculateDimensions(): Triple<Int, Int, Point> {
        var currentX = 0
        var currentY = 0

        var maxX = Int.MIN_VALUE
        var maxY = Int.MIN_VALUE

        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE

        commands.forEach { command ->
            when (command.direction) {
                Direction.TOP -> currentY -= command.count
                Direction.BOTTOM -> currentY += command.count
                Direction.LEFT -> currentX -= command.count
                Direction.RIGHT -> currentX += command.count
            }

            maxX = max(maxX, currentX)
            maxY = max(maxY, currentY)
            minX = min(minX, currentX)
            minY = min(minY, currentY)
        }

        val maxIndexX = if (minX < 0) maxX + (-minX) else maxX
        val maxIndexY = if (minY < 0) maxY + (-minY) else maxY
        return Triple(
            first = maxIndexX + 1,
            second = maxIndexY + 1,
            third = Point(x = abs(minX), y = abs(minY)),
        )
    }
}

private class CaveDigger(private val grid: Grid2D<CaveTerrain>) {

    data class DiggedCave(val grid: Grid2D<CaveTerrain>) {
        fun numberOfTiles(tile: CaveTerrain): Int = grid.sumOf { if (it == tile) 1.toInt() else 0 }
    }

    private enum class DigState(val char: Char) {
        UNKNOWN(' '), DUG_OUT('#'), SOLID('.');

        fun toCaveTerrain(): CaveTerrain = when (this) {
            UNKNOWN -> throw IllegalArgumentException("Unknown is not a valid CaveTerrain")
            DUG_OUT -> CaveTerrain.DUG_OUT
            SOLID -> CaveTerrain.SOLID
        }
    }

    fun digOutCaves(): DiggedCave {
        val digStates: MutableGrid2D<DigState> = grid.mapValues {
            if (it == CaveTerrain.SOLID) DigState.UNKNOWN else DigState.DUG_OUT
        }.toMutableGrid()

        // go through all nodes that are not visited yet (unknown) and fill the area it is part of
        digStates.forEachIndexed { point: Point, element: DigState ->
            if (element == DigState.UNKNOWN) digStates.fillArea(point)
        }

        return DiggedCave(digStates.mapValues { it.toCaveTerrain() })
    }

    /**
     * Fills the area of the given point.
     */
    private fun MutableGrid2D<DigState>.fillArea(start: Point) {
        var areaIsOutside = false
        val nodes = mutableSetOf<Point>()
        val nodesToTravelTo = mutableSetOf(start)

        // Starting from the initial point visit all neighbour points until a tunnel is reached.
        // Store all visited nodes as they are part of the current area.
        while (nodesToTravelTo.isNotEmpty()) {
            val currentPoint = nodesToTravelTo.first()
            nodesToTravelTo.remove(currentPoint)

            if (nodes.contains(currentPoint)) continue // already visited
            if (!grid.contains(currentPoint)) {
                // hit the outside of the grid
                areaIsOutside = true
                nodes.add(currentPoint)
            } else {
                // found another valid point
                val element = grid[currentPoint]
                if (element == CaveTerrain.DUG_OUT) continue // element is dug out and therefore a border

                nodes.add(currentPoint)

                nodesToTravelTo.add(currentPoint.copy(x = currentPoint.x - 1))
                nodesToTravelTo.add(currentPoint.copy(x = currentPoint.x + 1))
                nodesToTravelTo.add(currentPoint.copy(y = currentPoint.y - 1))
                nodesToTravelTo.add(currentPoint.copy(y = currentPoint.y + 1))
            }
        }

        // fill the calculated area to be either solid or dug out
        nodes.forEach { point ->
            if (this.contains(point)) {
                this[point] = if (areaIsOutside) DigState.SOLID else DigState.DUG_OUT
            }
        }
    }
}

private data class DigCommand(
    val direction: Direction,
    val count: Int,
)

private enum class CaveTerrain(val char: Char) {
    DUG_OUT('#'),
    SOLID('.'),
}
