package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.logging.GreyScaleMode
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.logging.greyscale
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.printIndexed
import de.devdudes.aoc.helpers.toGrid
import de.devdudes.aoc.helpers.toMutableGrid

class Day16 : Day(
    description = 16 - "Reindeer Maze",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Lowest Score to reach the End",
            input = "day16",
            testInput = "day16_test",
            expectedTestResult = 7_036,
            solutionResult = 95_444,
            solution = { input ->
                parseReindeerMaze(input).calculateMinScoreToEnd()
            }
        )

        puzzle(
            description = 2 - "Number of Tiles of all shortest Paths",
            input = "day16",
            testInput = "day16_test",
            expectedTestResult = 45,
            solutionResult = 513,
            solution = { input ->
                parseReindeerMaze(input).calculateNumberOfTilesThatArePartOfTheShortestWays()
            }
        )
    }
)

private fun parseReindeerMaze(input: List<String>): ReindeerMaze {
    var start = Point(-1, -1)
    var end = Point(-1, -1)
    val tiles = input.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            when (char) {
                '#' -> ReindeerMaze.Tile.Wall
                '.' -> ReindeerMaze.Tile.Path(scoreToEnd = Int.MAX_VALUE)
                'S' -> {
                    start = Point(x, y)
                    ReindeerMaze.Tile.Path(scoreToEnd = Int.MAX_VALUE)
                }

                'E' -> {
                    end = Point(x, y)
                    ReindeerMaze.Tile.Path(scoreToEnd = Int.MAX_VALUE)
                }

                else -> error("Invalid Reindeer Maze tile: $char")
            }
        }
    }
    return ReindeerMaze(tiles.toGrid(), start, end)
}

private class ReindeerMaze(
    private val tiles: Grid2D<Tile>,
    private val start: Point,
    private val end: Point,
) {

    sealed class Tile {
        data object Wall : Tile()
        data class Path(val scoreToEnd: Int) : Tile()
    }

    private fun Grid2D<Tile>.getPathTile(point: Point): Tile.Path = get(point) as? Tile.Path ?: error("tile is not a path")

    fun calculateMinScoreToEnd(): Int {
        tiles.printMaze()
        val solvedMaze = solveMazeWithDijkstra()
        solvedMaze.printMaze()
        return solvedMaze.maze.getPathTile(end).scoreToEnd
    }

    fun calculateNumberOfTilesThatArePartOfTheShortestWays(): Int {
        val solvedMaze = solveMazeWithDijkstra()
        solvedMaze.printMaze()
        return solvedMaze.getTilesOfShortestPath().size
    }

    data class PointWithDirection(val point: Point, val direction: Direction)

    data class SolvedMaze(val maze: Grid2D<Tile>, val end: Point, val waysToTile: Map<PointWithDirection, List<PointWithDirection>>) {
        fun getTilesOfShortestPath(): List<Point> {
            val stack = mutableListOf(PointWithDirection(end, Direction.TOP))
            val tiles = mutableListOf(PointWithDirection(end, Direction.TOP))
            while (stack.isNotEmpty()) {
                val step = stack.removeLast()
                waysToTile[step]?.forEach { next ->
                    if (!tiles.contains(next)) {
                        tiles.add(next)
                        stack.add(next)
                    }
                }
            }
            return tiles.map { it.point }.distinct()
        }
    }

    private fun solveMazeWithDijkstra(): SolvedMaze {
        data class QueuedItem(val score: Int, val point: Point, val direction: Direction)

        val solvedMaze = tiles.toMutableGrid()
        val tilesToVisit = mutableListOf(QueuedItem(0, start, Direction.RIGHT))
        solvedMaze[start] = solvedMaze.getPathTile(start).copy(scoreToEnd = 0)

        val waysToTile = mutableMapOf<PointWithDirection, List<PointWithDirection>>()
        val scoreAtPointWithDirection = mutableMapOf<PointWithDirection, Int>()

        while (tilesToVisit.isNotEmpty()) {
            val (score, point, direction) = tilesToVisit.removeFirst()

            listOf(
                direction to 1,
                direction.turnLeft() to 1001,
                direction.turnRight() to 1001,
            ).forEach { (nextDirection, scoreToAdd) ->
                val nextPoint = point.move(nextDirection)
                when (val tile = solvedMaze[nextPoint]) {
                    Tile.Wall -> Unit
                    is Tile.Path -> {
                        val nextScore = score + scoreToAdd
                        val nextKey = PointWithDirection(nextPoint, nextDirection)
                        val tileScore = scoreAtPointWithDirection.getOrDefault(nextKey, Int.MAX_VALUE)

                        // update min score on maze
                        if (nextScore < tile.scoreToEnd) {
                            solvedMaze[nextPoint] = tile.copy(scoreToEnd = nextScore)
                        }

                        if (nextScore < tileScore) {
                            // update tile with lower score then before
                            scoreAtPointWithDirection[nextKey] = nextScore
                            waysToTile[nextKey] = listOf(PointWithDirection(point, direction))
                            tilesToVisit.add(QueuedItem(nextScore, nextPoint, nextDirection))
                        } else if (nextScore == tileScore) {
                            // add new/another way to the tile
                            waysToTile.putIfAbsent(nextKey, emptyList())
                            waysToTile[nextKey] = waysToTile.getValue(nextKey) + PointWithDirection(point, direction)
                        }
                    }
                }
            }

            // sort so we grab the lowest one next
            tilesToVisit.sortBy { it.score }
        }

        return SolvedMaze(maze = solvedMaze, end = end, waysToTile = waysToTile)
    }

    private fun SolvedMaze.printMaze() {
        maze.printMaze(getTilesOfShortestPath())
    }

    private fun Grid2D<Tile>.printMaze() {
        printMaze(emptyList())
    }

    private fun Grid2D<Tile>.printMaze(pathToEnd: List<Point> = emptyList()) {
        val maxScore = this.maxOf { (it as? Tile.Path)?.scoreToEnd ?: 0 }
        val isSolved = maxScore < Int.MAX_VALUE
        this.printIndexed { point, tile ->
            when {
                point == start -> "S".colored(if (pathToEnd.contains(point)) LogColor.Red else LogColor.Magenta)
                point == end -> "E".colored(if (pathToEnd.contains(point)) LogColor.Red else LogColor.Magenta)
                tile is Tile.Path -> {
                    val color = when {
                        pathToEnd.contains(point) -> LogColor.Red
                        isSolved -> greyscale(tile.scoreToEnd, maxScore, GreyScaleMode.LIGHT_TO_DARK)
                        else -> LogColor.Blue
                    }
                    "▪".colored(color)
                }

                tile is Tile.Wall -> "▒"
                else -> error("unsupported maze tile: $tile")
            }
        }
    }
}

