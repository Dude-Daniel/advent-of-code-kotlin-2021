package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.aoc2024.days.LabMap.MoveAroundResult.InfiniteLoopDetected
import de.devdudes.aoc.aoc2024.days.LabMap.MoveAroundResult.MovedOutOfMapBounds
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.positionOf
import de.devdudes.aoc.helpers.toMutableGrid

class Day06 : Day(
    description = 6 - "Guard Gallivant",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of Visited Tiles",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 41,
            solutionResult = 5_312,
            solution = { input ->
                LabMap(input).countVisitedTiles()
            }
        )

        puzzle(
            description = 2 - "Number of ways to construct a endless Loop",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 6,
            solutionResult = 1_748,
            solution = { input ->
                LabMap(input).countWaysToConstructInfiniteLoops()
            }
        )
    }
)

private class LabMap(private val input: List<String>) {

    data class LabTile(
        val isObstacle: Boolean,
        val visitCount: Int,
        val isStartingPoint: Boolean,
    ) {
        val visited = visitCount > 0
    }

    private fun parseMap(): Grid2D<LabTile> =
        input.map { row ->
            row.map { tile ->
                when (tile) {
                    '#' -> LabTile(isObstacle = true, visitCount = 0, isStartingPoint = false)
                    '.' -> LabTile(isObstacle = false, visitCount = 0, isStartingPoint = false)
                    else -> LabTile(isObstacle = false, visitCount = 1, isStartingPoint = true)
                }
            }
        }.toMutableGrid()

    fun countVisitedTiles(): Int = (moveGuardAroundTheMap(parseMap()) as MovedOutOfMapBounds).visitedTiles

    fun countWaysToConstructInfiniteLoops(): Int {
        val map = parseMap()

        var infiniteLoopCount = 0

        // make each tile an obstacle which is not already an obstacle or the starting point and see if it contains an infinite loop
        map.forEachIndexed { point: Point, labTile: LabTile ->
            if (!labTile.isStartingPoint && !labTile.isObstacle) {
                val newMap = map.toMutableGrid().apply {
                    set(point, labTile.copy(isObstacle = true))
                }

                if (moveGuardAroundTheMap(newMap) is InfiniteLoopDetected) infiniteLoopCount += 1
            }
        }

        return infiniteLoopCount
    }

    sealed class MoveAroundResult {
        data class MovedOutOfMapBounds(val visitedTiles: Int) : MoveAroundResult()
        data object InfiniteLoopDetected : MoveAroundResult()
    }

    private fun moveGuardAroundTheMap(initialMap: Grid2D<LabTile>): MoveAroundResult {
        val map = initialMap.toMutableGrid()

        var direction = Direction.TOP
        var position = map.positionOf { it.isStartingPoint } ?: error("Starting point not found")

        while (true) {
            val nextPosition = position.move(direction)
            val nextTile = map.getOrNull(nextPosition)

            when {
                nextTile == null -> {
                    // end found (guard stepped out of the grid/map)
                    return MovedOutOfMapBounds(map.count { it.visited })
                }

                nextTile.isObstacle -> direction = direction.turnRight()

                nextTile.visitCount >= 4 -> {
                    // if a tile was visited multiple times then the guard moves in an infinite loop
                    // simple approach as the guard may enter the tile from 4 directions we check if the tile was visited 4 times
                    return InfiniteLoopDetected
                }

                else -> {
                    map[nextPosition] = nextTile.copy(visitCount = nextTile.visitCount + 1)
                    position = nextPosition
                }
            }
        }
    }

}
