package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.mapValuesIndexed
import de.devdudes.aoc.helpers.moveBottom
import de.devdudes.aoc.helpers.moveLeft
import de.devdudes.aoc.helpers.moveRight
import de.devdudes.aoc.helpers.moveTop
import de.devdudes.aoc.helpers.toGrid
import de.devdudes.aoc.helpers.toMutableGrid

class Day10 : Day(
    description = 10 - "Hoof It",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of reachable Peaks",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 36,
            solutionResult = 548,
            solution = { input ->
                parseTrailMap(input).countPeaksToReach()
            }
        )

        puzzle(
            description = 2 - "Number of Ways to reach the Peaks",
            input = "day10",
            testInput = "day10_test",
            expectedTestResult = 81,
            solutionResult = 1_252,
            solution = { input ->
                parseTrailMap(input).countWaysToPeaks()
            }
        )
    }
)

private fun parseTrailMap(input: List<String>): TrailMap =
    input.map { line -> line.toCharArray().map { char -> char.digitToInt() } }
        .toGrid()
        .mapValuesIndexed { point, height -> MapPosition(height, point) }
        .let(::TrailMap)

private data class MapPosition(val height: Int, val point: Point) {
    val isStart: Boolean = height == 0
    val isEnd: Boolean = height == 9
}

private class TrailMap(private val data: Grid2D<MapPosition>) {

    data class MapTile(
        val position: MapPosition,
        val connections: List<Point>,
        val visited: Boolean,
        val waysToReachPeaks: Set<List<Point>>,
    )

    fun countPeaksToReach(): Int =
        solveMap(data)
            .filter { tile -> tile.position.isStart }
            .sumOf { tile -> tile.waysToReachPeaks.distinctBy { route -> route.first() }.size }

    fun countWaysToPeaks(): Int =
        solveMap(data)
            .filter { tile -> tile.position.isStart }
            .sumOf { it.waysToReachPeaks.size }

    private fun solveMap(map: Grid2D<MapPosition>): Grid2D<MapTile> {
        val mutableTrailMap = map.mapValuesIndexed { point, position ->
            MapTile(
                position = position,
                connections = map.neighbors(point),
                visited = false,
                waysToReachPeaks = emptySet(),
            )
        }.toMutableGrid()

        val startingPoints = mutableTrailMap.filter { it.position.isStart }
        startingPoints.forEach { start ->
            val waysToPeaks = findWaysToPeaks(mutableTrailMap, start)
            mutableTrailMap[start.position.point] = start.copy(waysToReachPeaks = waysToPeaks, visited = true)
        }

        return mutableTrailMap
    }

    private fun findWaysToPeaks(heightMap: MutableGrid2D<MapTile>, tile: MapTile): Set<List<Point>> {
        if (tile.position.isEnd) return setOf(listOf(tile.position.point))
        if (tile.visited) return tile.waysToReachPeaks

        return tile.connections.flatMap { point ->
            val connectedTile = heightMap[point]
            val waysToPeaks = findWaysToPeaks(heightMap, connectedTile)
            if (waysToPeaks.isNotEmpty()) {
                val newWaysToPeaks = waysToPeaks.map { it + point }.toSet()
                heightMap[point] = connectedTile.copy(waysToReachPeaks = newWaysToPeaks, visited = true)
                newWaysToPeaks
            } else emptySet()
        }.toSet()
    }

    private fun Grid2D<MapPosition>.neighbors(point: Point): List<Point> {
        val current = get(point)
        val nextHeight = current.height + 1
        return buildList {
            point.moveTop().let { nextPoint -> if (getOrNull(nextPoint)?.height == nextHeight) add(nextPoint) }
            point.moveBottom().let { nextPoint -> if (getOrNull(nextPoint)?.height == nextHeight) add(nextPoint) }
            point.moveLeft().let { nextPoint -> if (getOrNull(nextPoint)?.height == nextHeight) add(nextPoint) }
            point.moveRight().let { nextPoint -> if (getOrNull(nextPoint)?.height == nextHeight) add(nextPoint) }
        }
    }
}
