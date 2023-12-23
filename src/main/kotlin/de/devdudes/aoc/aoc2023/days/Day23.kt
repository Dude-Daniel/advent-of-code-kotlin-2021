package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.background
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.printIndexed
import de.devdudes.aoc.helpers.toGrid

class Day23 : Day(
    description = 23 - "A Long Walk - Longest Hike",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "With Slippery Tiles",
            input = "day23",
            testInput = "day23_test",
            expectedTestResult = 94,
            solutionResult = Unit,
            solution = { input ->
                parseHikingMap(input = input, hasSlipperyTiles = true)
                    .getLongestHikingTrail()
            }
        )

        puzzle(
            description = 2 - "Without Slippery Tiles",
            input = "day23",
            testInput = "day23_test",
            expectedTestResult = 154,
            solutionResult = 6286,
            solution = { input ->
                parseHikingMap(input = input, hasSlipperyTiles = false)
                    .getLongestHikingTrail()
            }
        )
    }
)

private fun parseHikingMap(input: List<String>, hasSlipperyTiles: Boolean): HikingMap =
    input.map { row ->
        row.toCharArray().map {
            val tile = HikingTile.valueOf(it)
            if (!hasSlipperyTiles && tile is HikingTile.Slope) HikingTile.Path
            else tile
        }
    }.toGrid().let(::HikingMap)

private data class HikingMap(val map: Grid2D<HikingTile>) {

    val startPoint: Point = Point(1, 0)
    val endPoint: Point = Point(map.columns - 2, map.rows - 1)

    fun getLongestHikingTrail(): Int {
        val nodes = findNodes()
        var edges = getEdges(nodes)

        // skip last node to improve calculation speed by factor 2
        val edgeBeforeEnd = edges.first { it.end == endPoint }
        edges = edges - edgeBeforeEnd

        val edgesByStartPoint = edges.groupBy { it.start }

        val trail = findLongestPath(
            edges = edgesByStartPoint,
            visitedNodes = listOf(startPoint),
            end = edgeBeforeEnd.start,
        ) ?: throw IllegalStateException("a path must exist")

        printNodes(nodes = nodes, trail = trail)

        // add distance of dropped edge
        return trail.distance + edgeBeforeEnd.distance
    }

    private val cache = mutableMapOf<List<Point>, HikingTrail?>()
    private fun findLongestPath(
        edges: Map<Point, List<HikingGraphEdge>>,
        visitedNodes: List<Point>,
        end: Point,
    ): HikingTrail? {
        if (cache.containsKey(visitedNodes)) return cache[visitedNodes]

        val currentNode = visitedNodes.last()
        if (currentNode == end) {
            return HikingTrail(emptyList(), 0)
        }

        val connectedEdges = edges.getValue(currentNode)
        return connectedEdges.mapNotNull { edge ->
            if (edge.end in visitedNodes) {
                null
            } else {
                val trailToEnd = findLongestPath(edges, visitedNodes + edge.end, end)
                cache[visitedNodes + edge.end] = trailToEnd

                trailToEnd?.let { trail ->
                    HikingTrail(
                        listOf(element = edge) + trail.path,
                        edge.distance + trail.distance,
                    )
                }
            }
        }.maxByOrNull { it.distance }
    }

    private fun findNodes(): Set<Point> {
        val nodes = mutableSetOf(startPoint, endPoint)
        map.forEachIndexed { point: Point, tile: HikingTile ->
            if (tile !is HikingTile.Forest) {
                var connectingPaths = 0
                if (map.getOrNull(point.move(Direction.TOP)) !is HikingTile.Forest) connectingPaths++
                if (map.getOrNull(point.move(Direction.BOTTOM)) !is HikingTile.Forest) connectingPaths++
                if (map.getOrNull(point.move(Direction.LEFT)) !is HikingTile.Forest) connectingPaths++
                if (map.getOrNull(point.move(Direction.RIGHT)) !is HikingTile.Forest) connectingPaths++
                if (connectingPaths > 2) nodes.add(point)
            }
        }
        return nodes
    }

    private fun getEdges(nodes: Set<Point>): Set<HikingGraphEdge> {
        val edges = mutableListOf<HikingGraphEdge>()

        data class VisitedNode(val history: List<Point>, val direction: Direction, val distance: Int)
        nodes.forEach { nodePoint ->
            val nextTiles = mutableListOf(
                VisitedNode(listOf(nodePoint), Direction.TOP, 0),
                VisitedNode(listOf(nodePoint), Direction.BOTTOM, 0),
                VisitedNode(listOf(nodePoint), Direction.LEFT, 0),
                VisitedNode(listOf(nodePoint), Direction.RIGHT, 0),
            )

            while (nextTiles.isNotEmpty()) {
                val (history, enterDirection, distance) = nextTiles.removeFirst()
                val point = history.last()
                if (point in nodes && point != nodePoint) {
                    // end is reached (we found another node)
                    if (point != nodePoint) {
                        // end is not the start so we found an edge
                        edges.add(HikingGraphEdge(nodePoint, point, distance, history))
                    }
                } else {
                    // traverse next points
                    val currentTile = map.getOrNull(point)
                    if (currentTile == null || currentTile is HikingTile.Forest) {
                        // no valid step is possible
                    } else {
                        if (currentTile == HikingTile.Path) {
                            enterDirection.invert().others().forEach { nextDirection ->
                                nextTiles.add(
                                    VisitedNode(
                                        history = history + point.move(nextDirection),
                                        direction = nextDirection,
                                        distance = distance + 1,
                                    )
                                )
                            }
                        } else {
                            if (currentTile is HikingTile.Slope.North && enterDirection == Direction.TOP
                                || currentTile is HikingTile.Slope.South && enterDirection == Direction.BOTTOM
                                || currentTile is HikingTile.Slope.East && enterDirection == Direction.RIGHT
                                || currentTile is HikingTile.Slope.West && enterDirection == Direction.LEFT
                            ) {
                                nextTiles.add(
                                    VisitedNode(
                                        history = history + point.move(enterDirection),
                                        direction = enterDirection,
                                        distance = distance + 1,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        return edges.toSet()
    }

    private fun printNodes(nodes: Set<Point>, trail: HikingTrail) {
        map.printIndexed { point, tile ->
            val output = tile.tileChar.toString()
            val trailPoints = trail.points.toSet()

            when {
                tile == HikingTile.Forest ->
                    output.background(LogColor.GreyScale.Grey18).colored(LogColor.GreyScale.Grey18)

                point in nodes -> output.background(LogColor.Red).colored(LogColor.Black)
                point in trailPoints -> output.background(LogColor.Blue).colored(LogColor.Black)
                else -> output.background(LogColor.Green).colored(LogColor.Black)
            }
        }
    }
}

private data class HikingGraphEdge(
    val start: Point,
    val end: Point,
    val distance: Int,
    val pathPoints: List<Point>,
)

private data class HikingTrail(val path: List<HikingGraphEdge>, val distance: Int) {
    val points: List<Point> by lazy { path.flatMap { it.pathPoints } }
}

private sealed class HikingTile(val tileChar: Char) {
    data object Path : HikingTile('.')
    data object Forest : HikingTile('#')

    sealed class Slope(tileChar: Char) : HikingTile(tileChar) {
        data object North : Slope('^')
        data object South : Slope('v')
        data object East : Slope('>')
        data object West : Slope('<')
    }

    companion object {
        fun values(): Array<HikingTile> {
            return arrayOf(Path, Forest, Slope.North, Slope.South, Slope.East, Slope.West)
        }

        fun valueOf(value: Char): HikingTile = values().first { it.tileChar == value }
    }
}
