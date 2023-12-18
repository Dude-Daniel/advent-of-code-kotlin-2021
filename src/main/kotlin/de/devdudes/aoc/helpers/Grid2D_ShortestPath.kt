package de.devdudes.aoc.helpers

import java.util.PriorityQueue

fun <GridData, NodeData> Grid2D<GridData>.findShortestPath(
    start: Point,
    startData: NodeData,
    end: Point,
    endCondition: (data: NodeData, position: Point) -> Boolean = { _, _ -> true },
    neighbours: (data: NodeData, position: Point) -> Iterable<Pair<NodeData, Point>>,
    cost: (data: NodeData, position: Point) -> Int,
): ShortestPath<NodeData> {

    data class PositionedValue(val value: NodeData, val position: Point)

    fun ShortestPathInGraph<PositionedValue>.toShortestPath(): ShortestPath<NodeData> {
        val path = getPath().map { value ->
            ShortestPathEntry(
                point = value.position,
                value = value.value,
                score = result[value]!!.cost,
            )
        }
        return ShortestPath(start = start, end = end, path = path, score = getScore())
    }

    val pathInGraph = findShortestPathByPredicate(
        start = PositionedValue(value = startData, position = start),
        isEnd = { (node, point) -> point == end && endCondition(node, point) },
        obtainNeighbours = { node ->
            neighbours(node.value, node.position)
                .filter { (_, point) -> contains(point) }
                .map { (node, point) -> PositionedValue(node, point) }
        },
        cost = { _, next -> cost(next.value, next.position) },
    )

    return pathInGraph.toShortestPath()
}

data class ShortestPathEntry<T>(val point: Point, val value: T, val score: Int)

data class ShortestPath<T>(
    val start: Point,
    val end: Point,
    val path: List<ShortestPathEntry<T>>,
    val score: Int,
)

/**
 * Implements A* search to find the shortest path between two vertices using a predicate to determine the ending vertex
 */
private fun <Data> findShortestPathByPredicate(
    start: Data,
    isEnd: (node: Data) -> Boolean,
    obtainNeighbours: (node: Data) -> Iterable<Data>,
    cost: (current: Data, next: Data) -> Int = { _, _ -> 1 },
): ShortestPathInGraph<Data> {
    val toVisit = PriorityQueue(listOf(ScoredNode(start, 0)))
    var endVertex: Data? = null
    val seenNodes: MutableMap<Data, SeenNode<Data>> = mutableMapOf(start to SeenNode(0, null))

    while (endVertex == null) {
        if (toVisit.isEmpty()) {
            return ShortestPathInGraph(start, null, seenNodes)
        }

        val (currentVertex, currentScore) = toVisit.remove()
        endVertex = if (isEnd(currentVertex)) currentVertex else null

        val nextPoints = obtainNeighbours(currentVertex)
            .filter { it !in seenNodes }
            .map { next -> ScoredNode(next, currentScore + cost(currentVertex, next)) }

        toVisit.addAll(nextPoints)
        seenNodes.putAll(nextPoints.associate { it.vertex to SeenNode(it.score, currentVertex) })
    }

    return ShortestPathInGraph(start, endVertex, seenNodes)
}

private data class SeenNode<K>(val cost: Int, val prev: K?)

private data class ScoredNode<K>(val vertex: K, val score: Int) : Comparable<ScoredNode<K>> {
    override fun compareTo(other: ScoredNode<K>): Int = (score).compareTo(other.score)
}

private class ShortestPathInGraph<T>(val start: T, val end: T?, val result: Map<T, SeenNode<T>>) {
    fun getScore(vertex: T) = result[vertex]?.cost ?: throw IllegalStateException("Result for $vertex not available")
    fun getScore() = end?.let { getScore(it) } ?: throw IllegalStateException("No path found")

    fun getPath() = end?.let { getPath(it, emptyList()) } ?: throw IllegalStateException("No path found")

    private tailrec fun getPath(endVertex: T, pathEnd: List<T>): List<T> {
        val previous = result[endVertex]?.prev

        return if (previous == null) {
            listOf(endVertex) + pathEnd
        } else {
            getPath(previous, listOf(endVertex) + pathEnd)
        }
    }
}
