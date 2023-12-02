package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day02 : Day(
    description = 2 - "Cube Conundrum",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum of IDs of possible games,",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 8,
            solutionResult = 2265,
            solution = { input ->
                val cubeGames = parseCubeGames(input)

                val maxColorCounts = mapOf(
                    "red" to 12,
                    "green" to 13,
                    "blue" to 14,
                )

                cubeGames.games.mapNotNull { game ->
                    if (game.containsInvalidColorCounts(maxColorCounts)) null else game.gameId
                }.sum()
            }
        )

        puzzle(
            description = 2 - "Sum of the power of the fewest number of cubes of each color",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 2286,
            solutionResult = 64097,
            solution = { input ->
                val cubeGames = parseCubeGames(input)

                // 1. multiply the max color counts of each game
                // 2. sum all the resulting values
                cubeGames.games.sumOf { game ->
                    game.getMaxColorsCounts()
                        .map { it.count }
                        .reduce { acc, i -> acc * i }
                }
            }
        )
    }
)

private fun parseCubeGames(input: List<String>): CubeGames =
    input.map { line ->
        val (gameName, rawSets) = line.split(": ")

        // get id from "Game XXX"
        val gameId = gameName.split(" ").last().toInt()

        // create sets from input (i.e. "3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green")
        val sets = rawSets.split("; ").map { singleRawSet ->
            singleRawSet.split(", ").map { rawCubeCount ->
                val (count, color) = rawCubeCount.split(" ")
                CubeWithCount(count = count.toInt(), color = color)
            }.let(::CubeSet)
        }

        CubeGame(gameId = gameId, cubeSets = sets)
    }.let(::CubeGames)

private data class CubeGames(
    val games: List<CubeGame>,
)

private data class CubeGame(
    val gameId: Int,
    val cubeSets: List<CubeSet>,
) {
    fun getMaxColorsCounts(): Set<CubeWithCount> =
        cubeSets.flatMap { it.colorsWithCount }
            .groupBy { it.color }
            .mapValues { (color, value) -> CubeWithCount(count = value.maxOf { it.count }, color = color) }
            .values.toSet()

    fun containsInvalidColorCounts(maxColorCounts: Map<String, Int>): Boolean =
        cubeSets.firstOrNull { set ->
            set.containsInvalidColorCounts(maxColorCounts)
        } != null
}

private data class CubeSet(
    val colorsWithCount: List<CubeWithCount>,
) {
    fun containsInvalidColorCounts(maxColorCounts: Map<String, Int>): Boolean =
        colorsWithCount.firstOrNull { cubeWithCount ->
            cubeWithCount.count > (maxColorCounts[cubeWithCount.color] ?: 0)
        } != null
}

private data class CubeWithCount(
    val count: Int,
    val color: String,
)
