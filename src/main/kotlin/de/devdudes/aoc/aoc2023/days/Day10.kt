package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.aoc2023.days.Pipe.Companion.toPipe
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Point

class Day10 : Day(
    description = 10 - "Pipe Maze",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Find Loop",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 8,
            solutionResult = 7097,
            solution = { input ->
                parsePipeMaze(input)
                    .findPipeLoop()
                    .points.size / 2
            }
        )

        puzzle(
            description = 2 - "Find Enclosing Points",
            input = "day10",
            testInput = "day10_test_second",
            expectedTestResult = 8,
            solutionResult = 355,
            solution = { input ->
                parsePipeMaze(input)
                    .findEnclosingPoints()
                    .size
            }
        )
    }
)

private fun parsePipeMaze(input: List<String>): PipeMaze =
    input.map { line -> line.toCharArray().map { it.toPipe() } }
        .let(::PipeMaze)

private data class PipeMaze(private val coordinates: List<List<Pipe>>) {

    private val columns: Int = coordinates.first().size

    fun get(point: Point): Pipe = coordinates[point.y][point.x]

    fun findEnclosingPoints(): List<Point> {
        val loop = findPipeLoop()

        // get the sequences for each row in the pipe loop
        val sections = loop.points.sortedBy { it.point.y }
            .groupBy { entry -> entry.point.y }
            .mapValues { (_, entries) ->
                findSectionsInRow(entries)
            }

        // for each row
        return sections.flatMap { (y, ranges) ->
            // check all points in all ranges and collect the points which are not in the loop
            ranges.flatMap { range ->
                range.mapNotNull { x ->
                    if (loop.get(x = x, y = y) == null) Point(x, y) else null
                }
            }
        }
    }

    /**
     * Finds all ranges which describe inner contents of the [PipeLoop] (for one row). This means that all
     * values within such a range are considered to be inside the shape of the [PipeLoop]. This is done by
     * scanning the row for crossings of the pipe network and evaluating the rotation changes of the pipes.
     * Each crossing can be done from bottom to top or from top to bottom. Pipes which enter the current
     * row and leave the row in the same direction are not considered to be crossing the row.
     * This is important as the points between such intersections may still be considered to be inside the
     * shape.
     *
     * The solution is to use the `the non-zero winding rule`. Any crossings going from bottom to top
     * will increase a counter whereas any crossings from top to bottom will decrease a counter. If the counter
     * reaches zero the current section is completed. The new section will then start with the next point
     * from the [PipeLoop].
     */
    private fun findSectionsInRow(entries: List<PipeLoop.Entry>): List<IntRange> {
        var counter = 0
        var startEntry: PipeLoop.Entry? = null

        return entries.sortedBy { it.point.x }
            .mapIndexedNotNull { x, entry ->
                when {
                    entry.horizontal -> Unit // nothing to do
                    entry.vertical -> {
                        // change counter by two as the whole row is traversed at once
                        counter += if (entry.enterDirection == Direction.BOTTOM) 2 else -2
                    }

                    entry.enterDirection == Direction.TOP -> counter -= 1
                    entry.enterDirection == Direction.BOTTOM -> counter += 1

                    entry.exitDirection == Direction.TOP -> counter += 1
                    entry.exitDirection == Direction.BOTTOM -> counter -= 1
                }

                // in case the counter reaches 0 or the last element of the row is reached emit a new sequence
                val currentStartEntry = startEntry
                if (currentStartEntry != null) {
                    if (counter == 0 || x == entries.lastIndex) {
                        startEntry = null
                        currentStartEntry.point.x..entry.point.x
                    } else {
                        null // emit nothing
                    }
                } else {
                    startEntry = entry
                    null // emit nothing
                }
            }
    }

    fun findPipeLoop(): PipeLoop {
        // get start point and the second point
        val startPoint = findStartPoint()
        var currentPoint = findSecondPoint()

        // calculate first direction based on the start point and the second point
        val startDelta = currentPoint - startPoint
        var lastDirection = when {
            startDelta.x < 0 -> Direction.LEFT
            startDelta.x > 0 -> Direction.RIGHT
            startDelta.y < 0 -> Direction.BOTTOM
            else -> Direction.TOP
        }

        // initialize the resulting points with the second point (first point needs to be added later as its previous
        // point is not known yet)
        val points = mutableListOf(
            PipeLoop.Entry(
                point = currentPoint,
                pipe = get(currentPoint),
                enterDirection = lastDirection.invert(),
                exitDirection = (get(currentPoint).connections - lastDirection.invert()).first(),
            )
        )

        // search for next points until the start is reached
        while (currentPoint != startPoint) {
            lastDirection = (get(currentPoint).connections - lastDirection.invert()).first()
            currentPoint = currentPoint.move(direction = lastDirection, steps = 1)

            if (currentPoint != startPoint) {
                points.add(
                    PipeLoop.Entry(
                        point = currentPoint,
                        pipe = get(currentPoint),
                        enterDirection = lastDirection.invert(),
                        exitDirection = (get(currentPoint).connections - lastDirection.invert()).first(),
                    )
                )
            }
        }

        // add the start point at the beginning using the directions of the last point
        points.add(
            0,
            PipeLoop.Entry(
                point = startPoint,
                pipe = get(startPoint),
                enterDirection = points.last().exitDirection.invert(),
                exitDirection = points.first().enterDirection.invert(),
            )
        )

        return PipeLoop(points)
    }

    /**
     * Finds the start point (indicated by an 'S').
     */
    private fun findStartPoint(): Point {
        // find the start index by joining all lines to a single line, finding the index and then calculating x and y
        val startIndex = coordinates.flatten().indexOfFirst { it == Pipe.Start }
        return Point(startIndex % columns, startIndex / columns)
    }

    /**
     * Finds the point after the starting point by looking into all directions and returning the first
     * point that successfully connects to the start point.
     */
    private fun findSecondPoint(): Point {
        val startPoint = findStartPoint()
        val x = startPoint.x
        val y = startPoint.y

        return when {
            // check right
            coordinates.getOrNull(y)?.getOrNull(x + 1)
                ?.connections?.contains(Direction.LEFT) == true -> Point(x = x + 1, y = y)

            // check left
            coordinates.getOrNull(y)?.getOrNull(x - 1)
                ?.connections?.contains(Direction.RIGHT) == true -> Point(x = x - 1, y = y)

            // check bottom
            coordinates.getOrNull(y + 1)?.getOrNull(x)
                ?.connections?.contains(Direction.TOP) == true -> Point(x = x, y = y + 1)

            // check top
            coordinates.getOrNull(y - 1)?.getOrNull(x)
                ?.connections?.contains(Direction.BOTTOM) == true -> Point(x = x, y = y - 1)

            else -> throw Exception("No matching point found")
        }
    }
}

private data class PipeLoop(val points: List<Entry>) {

    data class Entry(
        val point: Point,
        val pipe: Pipe,
        val enterDirection: Direction,
        val exitDirection: Direction,
    ) {
        val horizontal: Boolean = enterDirection == Direction.LEFT && exitDirection == Direction.RIGHT
                || enterDirection == Direction.RIGHT && exitDirection == Direction.LEFT

        val vertical: Boolean = enterDirection == Direction.TOP && exitDirection == Direction.BOTTOM
                || enterDirection == Direction.BOTTOM && exitDirection == Direction.TOP
    }

    fun get(x: Int, y: Int): Entry? = get((Point(x = x, y = y)))
    fun get(point: Point): Entry? = points.firstOrNull { it.point == point }
}


private sealed class Pipe {

    abstract val connections: List<Direction>

    data object None : Pipe() {
        override val connections: List<Direction> = emptyList()
    }

    data object Start : Pipe() {
        override val connections: List<Direction> =
            listOf(Direction.TOP, Direction.BOTTOM, Direction.LEFT, Direction.RIGHT)
    }

    data object Vertical : Pipe() {
        override val connections: List<Direction> = listOf(Direction.TOP, Direction.BOTTOM)
    }

    data object Horizontal : Pipe() {
        override val connections: List<Direction> = listOf(Direction.LEFT, Direction.RIGHT)
    }

    data object BendNorthEast : Pipe() {
        override val connections: List<Direction> = listOf(Direction.TOP, Direction.RIGHT)
    }

    data object BendNorthWest : Pipe() {
        override val connections: List<Direction> = listOf(Direction.TOP, Direction.LEFT)
    }

    data object BendSouthEast : Pipe() {
        override val connections: List<Direction> = listOf(Direction.BOTTOM, Direction.RIGHT)
    }

    data object BendSouthWest : Pipe() {
        override val connections: List<Direction> = listOf(Direction.BOTTOM, Direction.LEFT)
    }

    companion object {
        fun Char.toPipe(): Pipe = when (this) {
            'S' -> Start
            '|' -> Vertical
            '-' -> Horizontal
            'F' -> BendSouthEast
            '7' -> BendSouthWest
            'L' -> BendNorthEast
            'J' -> BendNorthWest
            else -> None
        }
    }
}

private fun Point.move(direction: Direction, steps: Int = 1): Point =
    when (direction) {
        Direction.TOP -> Point(x, y - steps)
        Direction.BOTTOM -> Point(x, y + steps)
        Direction.LEFT -> Point(x - steps, y)
        Direction.RIGHT -> Point(x + steps, y)
    }

private operator fun Point.minus(other: Point): Point = Point(x = x - other.x, y = y - other.y)

private enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT;

    fun invert(): Direction =
        when (this) {
            TOP -> BOTTOM
            BOTTOM -> TOP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
}
