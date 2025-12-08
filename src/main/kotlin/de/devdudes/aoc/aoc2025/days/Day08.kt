package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Point3D
import de.devdudes.aoc.helpers.combinations
import de.devdudes.aoc.helpers.distanceSquaredTo
import de.devdudes.aoc.helpers.productOf

class Day08 : Day(
    description = 8 - "Playground",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of Circuits after connecting X junction boxes",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 40,
            solutionResult = 83520,
            solution = { input ->
                PlaygroundDecoration(input).connectCircuits(if (isTest) 10 else 1000)
            }
        )

        puzzle(
            description = 2 - "Distance to wall after connecting all junction boxes to one circuit",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 25272,
            solutionResult = 1131823407,
            solution = { input ->
                PlaygroundDecoration(input).calculateDistanceToWall()
            }
        )
    }
)

private class PlaygroundDecoration(input: List<String>) {

    private val points = input.map { line ->
        val (x, y, z) = line.split(",").map(String::toInt)
        Point3D(x, y, z)
    }

    fun connectCircuits(amount: Int): Int {
        var connectionCount = 0
        var result = 0
        connectJunctionBoxes(
            afterNewConnection = { _, circuits ->
                connectionCount++

                if (connectionCount >= amount) {
                    result = circuits
                        .map { it.size }
                        .sortedDescending()
                        .take(3)
                        .productOf { it }
                }

                connectionCount < amount
            }
        )
        return result
    }

    fun calculateDistanceToWall(): Int {
        val remainingPoints = points.toMutableSet()
        var result = 0
        connectJunctionBoxes(
            afterNewConnection = { (pointFrom, pointTo), _ ->
                remainingPoints.remove(pointFrom)
                remainingPoints.remove(pointTo)

                if (remainingPoints.isEmpty()) {
                    result = pointFrom.x * pointTo.x
                }

                remainingPoints.isNotEmpty()
            }
        )
        return result
    }

    private fun connectJunctionBoxes(afterNewConnection: (Pair<Point3D, Point3D>, List<Set<Point3D>>) -> Boolean) {
        val squaredDistances = points.combinations()
            .associate { points ->
                val value = points.first() to points.last()
                val key = value.first.distanceSquaredTo(value.second)
                key to value
            }

        val sortedDistances = squaredDistances.toList()
            .sortedBy { it.first }

        val circuits = mutableListOf<MutableSet<Point3D>>()

        sortedDistances.forEach { (_, points) ->
            val setA = circuits.find { it.contains(points.first) }
            val setB = circuits.find { it.contains(points.second) }
            when {
                setA == null && setB == null -> {
                    // points not added yet, so create a new circuit
                    circuits.add(mutableSetOf(points.first, points.second))
                }

                setA == null && setB != null -> {
                    // add pointA to circuit of pointB
                    setB.add(points.first)
                }

                setA != null && setB == null -> {
                    // add pointA to circuit of pointB
                    setA.add(points.second)
                }

                setA != null && setB != null && setA != setB -> {
                    // points are already in different sets, so merge them into one
                    circuits.remove(setB)
                    setA.addAll(setB)
                }
            }

            if (!afterNewConnection(points, circuits)) {
                return
            }
        }
    }

}
