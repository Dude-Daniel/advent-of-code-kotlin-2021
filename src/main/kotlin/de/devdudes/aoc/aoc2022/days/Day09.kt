package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.aoc2022.days.RopeBridgeStep.*
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day09 : Day(
    description = 9 - "Rope Bridge",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of positions the tail visited",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 13,
            solutionResult = 5874,
            solution = { input ->
                val steps = parseRopeBridgeSteps(input)
                RopeBridgeSolver().findAllTailPositions(
                    steps = steps,
                    knotCount = 2,
                ).size
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = 1,
            solutionResult = 2467,
            solution = { input ->
                val steps = parseRopeBridgeSteps(input)
                RopeBridgeSolver().findAllTailPositions(
                    steps = steps,
                    knotCount = 10,
                ).size
            }
        )
    }
)

private fun parseRopeBridgeSteps(input: List<String>): List<RopeBridgeStep> = input.map { line ->
    val (direction, steps) = line.split(" ")
    when (direction) {
        "U" -> Up(steps.toInt())
        "D" -> Down(steps.toInt())
        "L" -> Left(steps.toInt())
        "R" -> Right(steps.toInt())
        else -> throw UnsupportedOperationException("Unknown direction: $direction")
    }
}

private sealed class RopeBridgeStep {
    abstract val count: Int

    data class Up(override val count: Int) : RopeBridgeStep()
    data class Down(override val count: Int) : RopeBridgeStep()
    data class Left(override val count: Int) : RopeBridgeStep()
    data class Right(override val count: Int) : RopeBridgeStep()
}

private data class RopeKnot(val name: String, var x: Int, var y: Int) {

    val tailPositions = mutableSetOf(x to y)

    fun moveInDirection(step: RopeBridgeStep) {
        when (step) {
            is Down -> y -= 1
            is Left -> x -= 1
            is Right -> x += 1
            is Up -> y += 1
        }
    }

    fun moveToKnot(knot: RopeKnot) {
        val headX = knot.x
        val headY = knot.y

        // 1. check if tail is neighbor
        if ((x == headX || x + 1 == headX || x - 1 == headX) &&
            (y == headY || y + 1 == headY || y - 1 == headY)
        ) return

        // 2. move tail
        when {
            // move diagonally
            x != headX && y != headY -> {
                x += if (x < headX) 1 else -1
                y += if (y < headY) 1 else -1
            }

            // move along x-axis
            x != headX -> {
                x += if (x < headX) 1 else -1
            }

            // move along y-axis
            y != headY -> {
                y += if (y < headY) 1 else -1
            }
        }

        // 3. record tail position
        tailPositions.add(x to y)
    }
}

private class RopeBridgeSolver {
    fun findAllTailPositions(steps: List<RopeBridgeStep>, knotCount: Int): Set<Pair<Int, Int>> {
        val knots = buildList { repeat(knotCount) { index -> add(RopeKnot(name = index.toString(), x = 0, y = 0)) } }
        val head = knots.first()
        val tail = knots.last()
        val knotPairs = knots
            .windowed(size = 2, step = 1)
            .map { it.first() to it.last() }

        steps.forEach { step ->
            repeat(step.count) {
                // 1. move head
                head.moveInDirection(step)

                // 2. move all knots
                knotPairs.forEach {
                    it.second.moveToKnot(it.first)
                }
            }
        }

        return tail.tailPositions
    }
}
