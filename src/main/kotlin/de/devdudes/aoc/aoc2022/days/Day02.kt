package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.aoc2022.days.Day02Constants.DRAW
import de.devdudes.aoc.aoc2022.days.Day02Constants.LOST
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_DRAW
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_LOSE
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_PAPER
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_ROCK
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_SCISSORS
import de.devdudes.aoc.aoc2022.days.Day02Constants.ME_WIN
import de.devdudes.aoc.aoc2022.days.Day02Constants.OP_PAPER
import de.devdudes.aoc.aoc2022.days.Day02Constants.OP_ROCK
import de.devdudes.aoc.aoc2022.days.Day02Constants.OP_SCISSORS
import de.devdudes.aoc.aoc2022.days.Day02Constants.PAPER_VALUE
import de.devdudes.aoc.aoc2022.days.Day02Constants.ROCK_VALUE
import de.devdudes.aoc.aoc2022.days.Day02Constants.SCISSORS_VALUE
import de.devdudes.aoc.aoc2022.days.Day02Constants.WIN
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.lang.UnsupportedOperationException

class Day02 : Day(
    description = 2 - "Rock Paper Scissors",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "strategy guide with shape input",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 15,
            solutionResult = 12156,
            solution = { input ->
                val gameResults = input.map { line ->
                    val (opponent, me) = line.split(" ")

                    when (opponent) {
                        OP_ROCK -> when (me) {
                            ME_ROCK -> DRAW + ROCK_VALUE
                            ME_PAPER -> WIN + PAPER_VALUE
                            ME_SCISSORS -> LOST + SCISSORS_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        OP_PAPER -> when (me) {
                            ME_ROCK -> LOST + ROCK_VALUE
                            ME_PAPER -> DRAW + PAPER_VALUE
                            ME_SCISSORS -> WIN + SCISSORS_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        OP_SCISSORS -> when (me) {
                            ME_ROCK -> WIN + ROCK_VALUE
                            ME_PAPER -> LOST + PAPER_VALUE
                            ME_SCISSORS -> DRAW + SCISSORS_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        else -> throw UnsupportedOperationException("opponent value not supported $opponent")
                    }
                }

                gameResults.sum()
            }
        )

        puzzle(
            description = 2 - "strategy guide with expected result input",
            input = "day02",
            testInput = "day02_test",
            expectedTestResult = 12,
            solutionResult = 10835,
            solution = { input ->
                val gameResults = input.map { line ->
                    val (opponent, me) = line.split(" ")

                    when (opponent) {
                        OP_ROCK -> when (me) {
                            ME_LOSE -> LOST + SCISSORS_VALUE
                            ME_DRAW -> DRAW + ROCK_VALUE
                            ME_WIN -> WIN + PAPER_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        OP_PAPER -> when (me) {
                            ME_LOSE -> LOST + ROCK_VALUE
                            ME_DRAW -> DRAW + PAPER_VALUE
                            ME_WIN -> WIN + SCISSORS_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        OP_SCISSORS -> when (me) {
                            ME_LOSE -> LOST + PAPER_VALUE
                            ME_DRAW -> DRAW + SCISSORS_VALUE
                            ME_WIN -> WIN + ROCK_VALUE
                            else -> throw UnsupportedOperationException("me value not supported $opponent")
                        }

                        else -> throw UnsupportedOperationException("opponent value not supported $opponent")
                    }
                }

                gameResults.sum()
            }
        )
    }
)

private object Day02Constants {
    const val DRAW = 3
    const val LOST = 0
    const val WIN = 6

    const val OP_ROCK = "A"
    const val OP_PAPER = "B"
    const val OP_SCISSORS = "C"

    const val ME_ROCK = "X"
    const val ME_PAPER = "Y"
    const val ME_SCISSORS = "Z"

    const val ME_LOSE = "X"
    const val ME_DRAW = "Y"
    const val ME_WIN = "Z"

    const val ROCK_VALUE = 1
    const val PAPER_VALUE = 2
    const val SCISSORS_VALUE = 3
}
