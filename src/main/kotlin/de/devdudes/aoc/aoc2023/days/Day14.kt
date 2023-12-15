package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.transpose

class Day14 : Day(
    description = 14 - "Parabolic Reflector Dish - Rock Total Load",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Slide North",
            input = "day14",
            testInput = "day14_test",
            expectedTestResult = 136,
            solutionResult = 113_078,
            solution = { input ->
                parseParabolicReflectorDish(input)
                    .tiltNorth()
                    .calculateTotalLoad()
            }
        )

        puzzle(
            description = 2 - "Slide in Cycles",
            input = "day14",
            testInput = "day14_test",
            expectedTestResult = 64,
            solutionResult = 94_255,
            solution = { input ->
                parseParabolicReflectorDish(input)
                    .calculateTotalLoadAtCycle(1_000_000_000)
            }
        )
    }
)

private fun parseParabolicReflectorDish(input: List<String>): ParabolicReflectorDish =
    input.map { row ->
        row.map { tile ->
            when (tile) {
                'O' -> ReflectorDishTile.ROUNDED_ROCK
                '#' -> ReflectorDishTile.CUBE_SHAPED_ROCK
                else -> ReflectorDishTile.EMPTY
            }
        }
    }.let(::ParabolicReflectorDish)

private data class ParabolicReflectorDish(val grid: List<List<ReflectorDishTile>>) {

    fun calculateTotalLoad(): Int =
        grid.mapIndexed { index, row ->
            val pointsPerTile = grid.size - index
            row.count { it == ReflectorDishTile.ROUNDED_ROCK } * pointsPerTile
        }.sum()

    fun calculateTotalLoadAtCycle(cycles: Int): Int =
        findTiltCycleRepetitions().firstNotNullOf { (dish, repetitions) ->
            val (start, end) = repetitions
            val distanceBetweenRepetitions = end.counter - start.counter
            val distanceToTravelByFullRepetitions = cycles - start.counter

            // check if the cycle counts of the current repetition will at some point match the cycles needed
            if (distanceToTravelByFullRepetitions % distanceBetweenRepetitions == 0) {
                dish.calculateTotalLoad()
            } else null
        }

    private data class Repetition(val counter: Int, val grid: List<List<ReflectorDishTile>>, val totalLoad: Int)

    /**
     * When the [ParabolicReflectorDish] is tilted in cycles for enough times then the
     * resulting [ParabolicReflectorDish] will be the same over and over again. This functions calculates
     * these repetitions and their length (the number of cycles needed to reach the exact same reflector dish).
     */
    private fun findTiltCycleRepetitions(): MutableMap<ParabolicReflectorDish, MutableList<Repetition>> {
        val repetitions = mutableMapOf<ParabolicReflectorDish, MutableList<Repetition>>()

        var currentDish = this
        var counter = 0
        var cycleFound = false

        // search until all repetitions were found at least twice (so we can calculate the distance between them)
        while (repetitions.isEmpty() || repetitions.any { it.value.size < 2 }) {
            currentDish = currentDish.tiltInCycle()
            counter += 1

            val repetition = Repetition(
                counter = counter,
                grid = currentDish.grid,
                totalLoad = currentDish.calculateTotalLoad(),
            )

            val repetitionsOfDish = repetitions.getOrPut(currentDish) { mutableListOf() }
            if (!cycleFound && repetitionsOfDish.isNotEmpty()) {
                // if one cycle was found and none was found so far then clear all repetitions
                // and add the current dish as the new element in the resulting repetitions.
                // All elements found so far will never have any repetition as the grid needs to settle first
                // before repeating patterns
                repetitions.clear()
                repetitions[currentDish] = mutableListOf(repetition)
                cycleFound = true
            } else {
                repetitionsOfDish.add(repetition)
            }
        }

        return repetitions
    }

    fun tiltNorth(): ParabolicReflectorDish = grid.tiltNorth().let(::ParabolicReflectorDish)

    /**
     * Tilts the [ParabolicReflectorDish] in all directions in that order: North, West, South, East.
     * Returns the resulting [ParabolicReflectorDish].
     */
    private fun ParabolicReflectorDish.tiltInCycle(): ParabolicReflectorDish =
        grid.tiltNorth()
            .tiltWest()
            .tiltSouth()
            .tiltEast()
            .let(::ParabolicReflectorDish)

    private fun List<List<ReflectorDishTile>>.tiltNorth(): List<List<ReflectorDishTile>> =
        transpose().map { row -> row.tiltToStart() }.transpose()

    private fun List<List<ReflectorDishTile>>.tiltWest(): List<List<ReflectorDishTile>> =
        map { row -> row.tiltToStart() }

    private fun List<List<ReflectorDishTile>>.tiltSouth(): List<List<ReflectorDishTile>> =
        transpose().map { row -> row.reversed().tiltToStart().reversed() }.transpose()

    private fun List<List<ReflectorDishTile>>.tiltEast(): List<List<ReflectorDishTile>> =
        map { row -> row.reversed().tiltToStart().reversed() }

    /**
     * Tilts a row to its start (to the left). This means that all rounded rocks roll to the start until they
     * either are placed at the start or hit an obstacle (another rounded rock or a cube shaped rock).
     */
    private fun List<ReflectorDishTile>.tiltToStart(): List<ReflectorDishTile> {
        val tiltedRow = toMutableList()
        for (index in indices) {
            // find new position
            val newIndex = findTiltedPositionIndex(tiltedRow, index)

            // if new position exists then move the current rounded rock to that position
            if (newIndex != null) {
                tiltedRow[newIndex] = ReflectorDishTile.ROUNDED_ROCK
                tiltedRow[index] = ReflectorDishTile.EMPTY
            }
        }
        return tiltedRow
    }

    /**
     * Finds the new index of [ReflectorDishTile] (defined by [index]) in the given [elements] list.
     * The new position is calculated so that the tile touches the next blocking element to the start
     * of the list. A blocking element is either another rounded rock or a cube shaped rock.
     *
     * If no new position is found (or its the same as the current position) than null is returned.
     */
    private fun findTiltedPositionIndex(elements: List<ReflectorDishTile>, index: Int): Int? {
        val tile = elements[index]
        if (tile == ReflectorDishTile.ROUNDED_ROCK) {
            for (newIndex in index - 1 downTo 0) {
                val targetTile = elements[newIndex]
                when {
                    targetTile == ReflectorDishTile.ROUNDED_ROCK
                            || targetTile == ReflectorDishTile.CUBE_SHAPED_ROCK -> {
                        return if (newIndex + 1 == index) null else newIndex + 1
                    }

                    targetTile == ReflectorDishTile.EMPTY && newIndex == 0 ->
                        return 0
                }
            }
        }
        return null
    }
}

private enum class ReflectorDishTile {
    ROUNDED_ROCK, CUBE_SHAPED_ROCK, EMPTY
}
