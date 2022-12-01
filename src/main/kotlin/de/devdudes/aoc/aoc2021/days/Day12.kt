package de.devdudes.aoc.aoc2021.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day12 : Day(description = 12 - "Passage Pathing", {

    puzzle(
        description = 1 - "Visit small Caves once",
        input = "day12",
        testInput = "day12_test",
        expectedTestResult = 226,
        solutionResult = 3292,
        solution = { input ->
            val paths = CaveSystem(input).findAllPaths(0)
            paths.size
        }
    )

    puzzle(
        description = 2 - "Visit only one small Caves twice",
        input = "day12",
        testInput = "day12_test",
        expectedTestResult = 3509,
        solutionResult = 89592,
        solution = { input ->
            val paths = CaveSystem(input).findAllPaths(1)
            paths.size
        }
    )
})

private sealed class Cave() {
    abstract val name: String

    data class Big(override val name: String) : Cave()
    data class Small(override val name: String, val visited: Boolean = false, val visitCount: Int = 0) : Cave()

    val isStartCave: Boolean by lazy { name == START }

    companion object {
        private const val START: String = "start"
        private const val END: String = "end"

        val START_CAVE: Small = Small(START, visited = false)
        val END_CAVE: Small = Small(END, visited = false)

        fun fromName(name: String): Cave {
            return when {
                name == START -> START_CAVE
                name.first().isUpperCase() -> Big(name)
                else -> Small(name)
            }
        }
    }
}

private data class CavePath(val caves: List<Cave>) {
    fun addToStart(cave: Cave): CavePath {
        val newCaves = caves.toMutableList().apply { add(0, cave) }
        return this.copy(caves = newCaves)
    }
}

private data class CaveMap(val data: Map<Cave, Set<Cave>>) {
    fun connections(cave: Cave): Set<Cave>? =
        data.firstNotNullOfOrNull { entry -> if (entry.key.name == cave.name) entry.value else null }

    fun markVisited(cave: Cave.Small): CaveMap {
        val newData = data.mapValues { caves ->
            caves.value.map {
                if (it is Cave.Small && it.name == cave.name) {
                    it.copy(visited = true, visitCount = it.visitCount + 1)
                } else it
            }.toSet()
        }
        return CaveMap(newData)
    }
}

private class CaveSystem(connections: List<String>) {

    private val initialCaveConnections: CaveMap

    init {
        val caveConnections: MutableMap<Cave, Set<Cave>> = mutableMapOf()

        connections.forEach { connection ->
            val (from, to) = connection.split("-")
            val fromCave = Cave.fromName(from)
            val toCave = Cave.fromName(to)

            caveConnections.getOrDefault(fromCave, emptySet()).toMutableSet().also { caves ->
                caves.add(toCave)
                caveConnections[fromCave] = caves
            }

            caveConnections.getOrDefault(toCave, emptySet()).toMutableSet().also { caves ->
                caves.add(fromCave)
                caveConnections[toCave] = caves
            }
        }

        initialCaveConnections = CaveMap(caveConnections)
    }

    fun findAllPaths(duplicateVisits: Int): List<CavePath> {
        return traverseCaves(initialCaveConnections, Cave.START_CAVE, duplicateVisits)
    }

    private fun traverseCaves(caveMap: CaveMap, currentCave: Cave, duplicateVisits: Int): List<CavePath> {
        val connections = caveMap.connections(currentCave) ?: return emptyList()

        val foundPaths = when {
            currentCave == Cave.END_CAVE -> listOf(CavePath(listOf(Cave.END_CAVE)))

            !currentCave.isStartCave && currentCave is Cave.Small && currentCave.visited && duplicateVisits - currentCave.visitCount < 0 -> {
                // small and not allowed to be visited anymore (visited and no duplicate visits left)
                emptyList()
            }

            currentCave.isStartCave && currentCave is Cave.Small && currentCave.visited -> {
                // multiple visits on start cave are not allowed
                emptyList()
            }

            else -> {
                val newDuplicateVisits =
                    if (currentCave is Cave.Small && currentCave.visitCount > 0) duplicateVisits - 1 else duplicateVisits

                val newMap = if (currentCave is Cave.Small) {
                    caveMap.markVisited(currentCave)
                } else caveMap

                connections.flatMap { connectedCave -> traverseCaves(newMap, connectedCave, newDuplicateVisits) }
            }
        }

        return foundPaths.map { it.addToStart(currentCave) }
    }
}
