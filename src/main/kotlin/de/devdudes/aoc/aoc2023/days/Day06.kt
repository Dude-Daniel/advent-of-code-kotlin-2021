package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.reversed
import de.devdudes.aoc.helpers.transpose
import de.devdudes.aoc.helpers.until
import java.math.BigInteger

class Day06 : Day(
    description = 6 - "Wait For It - Boat Race - Number of ways to beat the record",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "multiple games",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 288,
            solutionResult = 316_800,
            solution = { input ->
                parseMultipleBoatRaceRecords(input)
                    .map { record ->
                        record.calculateWaysToBeatCurrentDistance()
                    }.reduce { acc, value -> acc * value }
            }
        )

        puzzle(
            description = 2 - "single game",
            input = "day06",
            testInput = "day06_test",
            expectedTestResult = 71_503,
            solutionResult = 45_647_654,
            solution = { input ->
                parseSingleBoatRaceRecord(input)
                    .calculateWaysToBeatCurrentDistance()
            }
        )
    }
)

private fun parseMultipleBoatRaceRecords(input: List<String>): List<BoatRaceRecord> =
    input.map { line ->
        line.split(Regex(" +")).drop(1)
    }.transpose()
        .map { (time, distance) -> BoatRaceRecord(time = time.toBigInteger(), distance = distance.toBigInteger()) }


private fun parseSingleBoatRaceRecord(input: List<String>): BoatRaceRecord =
    input.map { line ->
        line.substringAfter(":").replace(" ", "").toBigInteger()
    }.let { (time, distance) -> BoatRaceRecord(time = time, distance = distance) }

data class BoatRaceRecord(val time: BigInteger, val distance: BigInteger) {

    fun calculateWaysToBeatCurrentDistance(): Int {
        // skip first and last time as they result in 0 value anyway

        // calculate first time which wins
        val firstTimeToWin = (1.toBigInteger() until time).first { currentTime ->
            val speed = currentTime // one unit of speed per time unit
            val remainingTime = time - currentTime
            val newDistance = speed * remainingTime
            newDistance > distance
        }

        // calculate last time which wins
        val lastTimeToWin = (1.toBigInteger() until time).reversed().asSequence().first { currentTime ->
            val speed = currentTime // one unit of speed per time unit
            val remainingTime = time - currentTime
            val newDistance = speed * remainingTime
            newDistance > distance
        }

        // in between the first time that wins and the last time that wins all other times win as well
        return lastTimeToWin.toInt() - firstTimeToWin.toInt() + 1
    }
}
