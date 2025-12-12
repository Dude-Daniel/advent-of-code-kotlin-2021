package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day11 : Day(
    description = 11 - "Reactor",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of Paths leading from 'you' to 'out'",
            input = "day11",
            testInput = "day11_test_01",
            expectedTestResult = 5L,
            solutionResult = 615L,
            solution = { input ->
                ServerRack(input).findPathsFromYouToOut()
            }
        )

        puzzle(
            description = 2 - "Number of Paths leading from 'svr' to 'out' visiting 'dac' and 'fft'",
            input = "day11",
            testInput = "day11_test_02",
            expectedTestResult = 2L,
            solutionResult = 303_012_373_210_128L,
            solution = { input ->
                ServerRack(input).findPathsFromSvrToOut()
            }
        )
    }
)

private class ServerRack(input: List<String>) {

    private val connections = input.associate { line ->
        val (key, values) = line.split(":")
        key to values.trim().split(" ")
    }

    fun findPathsFromYouToOut(): Long =
        PathFinder(
            connections = connections,
            cacheData = { Unit },
            pathValue = { 1L },
        ).calculatePathCount(start = "you")

    fun findPathsFromSvrToOut(): Long =
        PathFinder(
            connections = connections,
            cacheData = { currentPath -> currentPath.contains("dac") to currentPath.contains("fft") },
            pathValue = { currentPath -> if (currentPath.contains("dac") && currentPath.contains("fft")) 1L else 0L },
        ).calculatePathCount(start = "svr")

    private class PathFinder<CacheData>(
        private val connections: Map<String, List<String>>,
        private val cacheData: (currentPath: List<String>) -> CacheData,
        private val pathValue: (currentPath: List<String>) -> Long,
    ) {

        data class CachedPathToEnd<Data>(val fromMachine: String, val data: Data)

        private val cache = mutableMapOf<CachedPathToEnd<CacheData>, Long>()

        fun calculatePathCount(start: String): Long {
            cache.clear()
            return calculatePossiblePaths(from = start, currentPath = emptyList())
        }

        private fun calculatePossiblePaths(from: String, currentPath: List<String>): Long {
            val key = CachedPathToEnd(from, data = cacheData(currentPath))
            if (cache.contains(key)) {
                return cache.getValue(key)
            }

            val nextDevices = connections.getValue(from)

            // continue current path by connecting the next devices
            // afterwards sum up all the total paths on the next devices
            val sumOf = nextDevices.sumOf { device ->
                when {
                    device == "out" -> {
                        // end reached -> one more path found
                        pathValue(currentPath)
                    }

                    currentPath.contains(device) -> 0L // loop detected

                    else -> {
                        // end is not reached -> continue
                        calculatePossiblePaths(device, currentPath + from)
                    }
                }
            }
            cache[key] = sumOf
            return sumOf
        }
    }
}
