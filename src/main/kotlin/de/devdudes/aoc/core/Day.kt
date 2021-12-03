package de.devdudes.aoc.core

/**
 * A collection of all puzzles for one day.
 */
abstract class Day(val description: Description, body: DayDsl.() -> Unit) {

    private val puzzles: List<Puzzle>

    init {
        val dayDsl = DayDsl()
        body(dayDsl)
        puzzles = dayDsl.build()
    }

    fun solve() {
        println()
        print("It's Day ${description.value} - Task of the day: ${description.name}")

        puzzles.forEach { puzzle ->
            with(puzzle) {
                printPuzzle("Solving Puzzle ${puzzle.description.value} - ${puzzle.description.name}")
                printPuzzle("Test Data - Expected Result: ${puzzle.expectedTestResult}")

                val testResult = puzzle.test()
                printPuzzle("Test Data - Actual Result: $testResult")

                check(testResult == puzzle.expectedTestResult) {
                    "Expected test result is incorrect -> solution is not implemented correctly."
                }
                printPuzzle("Test Data - Success! - Solution is working.")

                val result = puzzle.solve()
                printPuzzle("Puzzle Data Result: $result")

                if (puzzle.solutionResult != Unit) {
                    check(result == puzzle.solutionResult) {
                        "Expected puzzle solution is incorrect. Must be: ${puzzle.solutionResult}"
                    }

                    printPuzzle("Expected puzzle solution is correct.")
                }
            }
        }
    }

    private val dayTag: String = "[Day ${description.value} - ${description.name}]"

    private fun print(message: String) = println("$dayTag $message")
    private fun Puzzle.printPuzzle(message: String) {
        println("$dayTag [Puzzle ${description.value} - ${description.name}] $message")
    }
}
