package de.devdudes.aoc.core

import de.devdudes.aoc.core.Utils.formatDuration
import kotlin.system.measureTimeMillis

/**
 * A collection of all puzzles for one day.
 *
 * @param description description of the day.
 * @param ignored true if day is not implemented yet, else false.
 * @param days a DSL for implementing the [Puzzle]s of this day.
 */
abstract class Day(
    private val description: Description,
    val ignored: Boolean,
    days: DayDsl.() -> Unit,
) {

    constructor(description: Description, body: DayDsl.() -> Unit) : this(
        description = description,
        ignored = false,
        days = body,
    )

    private val puzzles: List<Puzzle>

    init {
        val dayDsl = DayDsl()
        days(dayDsl)
        puzzles = dayDsl.build()
    }

    /**
     * Solves all puzzles of the given day.
     *
     * @return true if puzzles are solved, false if the day is ignored.
     */
    fun solve(resourceFolder: String): Boolean {
        if (ignored) return false

        println()
        print("It's Day ${description.value} - Task of the day: ${description.name}")

        val totalDuration = measureTimeMillis {
            puzzles.forEach { puzzle ->
                val puzzleDuration = measureTimeMillis {
                    solvePuzzle(
                        puzzle = puzzle,
                        resourceFolder = resourceFolder,
                    )
                }
                puzzle.printPuzzle("Solved in: ${formatDuration(puzzleDuration)}")
            }
        }

        print("Day solved in: ${formatDuration(totalDuration)}")

        return true
    }

    private fun solvePuzzle(puzzle: Puzzle, resourceFolder: String) {
        with(puzzle) {
            printPuzzle("Solving Puzzle ${puzzle.description.value} - ${puzzle.description.name}")
            printPuzzle("Test Data - Expected Result: ${puzzle.expectedTestResult}")

            val testResult = puzzle.test(resourceFolder)
            printPuzzle("Test Data - Actual Result: $testResult")

            check(testResult == puzzle.expectedTestResult) {
                "Expected test result is incorrect -> solution is not implemented correctly."
            }
            printPuzzle("Test Data - Success! - Solution is working.")

            val result = puzzle.solve(resourceFolder)
            printPuzzle("Puzzle Data Result: $result")

            if (puzzle.solutionResult != Unit) {
                check(result == puzzle.solutionResult) {
                    "Expected puzzle solution is incorrect. Must be: ${puzzle.solutionResult}"
                }

                printPuzzle("Expected puzzle solution is correct.")
            }
        }
    }

    private val dayTag: String = "[Day ${description.value} - ${description.name}]"

    private fun print(message: String) = println("$dayTag $message")
    private fun Puzzle.printPuzzle(message: String) {
        println("$dayTag [Puzzle ${description.value} - ${description.name}] $message")
    }
}
