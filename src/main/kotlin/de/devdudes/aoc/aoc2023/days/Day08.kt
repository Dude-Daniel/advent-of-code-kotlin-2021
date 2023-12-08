package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.lcm

class Day08 : Day(
    description = 8 - "Haunted Wasteland",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Step Count from AAA to reach ZZZ",
            input = "day08",
            testInput = "day08_test",
            expectedTestResult = 6,
            solutionResult = 14893,
            solution = { input ->
                parseWastelandNetwork(input)
                    .countInstructionsFromTo("AAA", isEnd = { it == "ZZZ" })
            }
        )

        puzzle(
            description = 2 - "Step Count from **A to reach **Z",
            input = "day08",
            testInput = "day08_test_second",
            expectedTestResult = 6L,
            solutionResult = 10_241_191_004_509,
            solution = { input ->
                parseWastelandNetwork(input)
                    .countGhostInstructions()
            }
        )
    }
)

private fun parseWastelandNetwork(input: List<String>): WastelandNetwork {
    val instructions = input.first().map { if (it == 'L') WastelandDirection.L else WastelandDirection.R }
    val mappingNodes: Map<String, Pair<String, String>> = input.drop(2).map { line ->
        // split line: "AAA = (BBB, CCC)"
        val (from, to) = line.split(" = ")

        // remove "(" and ")"
        val (toLeft, toRight) = to.drop(1).dropLast(1)
            .split(", ")

        from to (toLeft to toRight)
    }.toMap()

    return WastelandNetwork(
        instructions = instructions,
        mappingNodes = mappingNodes,
    )
}

private enum class WastelandDirection { L, R }

private fun List<WastelandDirection>.endlessIterator(): Iterator<WastelandDirection> {
    return object : Iterator<WastelandDirection> {
        var currentIndex = -1

        override fun hasNext(): Boolean = true

        override fun next(): WastelandDirection {
            currentIndex = (currentIndex + 1) % size
            return get(currentIndex)
        }
    }
}

private data class WastelandNetwork(
    val instructions: List<WastelandDirection>,
    val mappingNodes: Map<String, Pair<String, String>>,
) {

    fun countInstructionsFromTo(start: String, isEnd: (String) -> Boolean): Int {
        var currentNode = start
        var count = 0
        val endlessInstructions = instructions.endlessIterator()

        while (!isEnd(currentNode)) {
            count += 1
            val node = mappingNodes.getValue(currentNode)

            currentNode = when (endlessInstructions.next()) {
                WastelandDirection.L -> node.first
                WastelandDirection.R -> node.second
            }
        }

        return count
    }

    fun countGhostInstructions(): Long {
        // find all nodes ending with "A"
        val startNodes = mappingNodes.keys.filter { it.endsWith("A") }

        // get distances for each start node until their end (node ending with "Z")
        val ranges = startNodes.map { node ->
            countInstructionsFromTo(start = node, isEnd = { it.endsWith("Z") }).toLong()
        }

        // calculate the total distance using the least common multiple
        return ranges.reduce { acc, value -> lcm(acc, value) }
    }
}
