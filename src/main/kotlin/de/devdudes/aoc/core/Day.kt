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
        print("It's Day ${description.value} - Task of the day: ${description.name}")

        puzzles.forEach { puzzle ->
            with(puzzle) {
                printPuzzle("Solving Puzzle ${puzzle.description.value} - ${puzzle.description.name}")
                printPuzzle("Processing Test Data")
                printPuzzle("Test Data - Expected Result: ${puzzle.expectedTestResult}")

                val testResult = puzzle.test()
                printPuzzle("Test Data - Actual Result: $testResult")

                check(testResult == puzzle.expectedTestResult)
                printPuzzle("Test Data - Success!")

                val result = puzzle.solve()
                printPuzzle("Puzzle Data Result: $result")
            }
        }
    }

    private fun print(message: String) = println("[Day ${description.value}] $message")
    private fun Puzzle.printPuzzle(message: String) =
        println("[Day ${this@Day.description.value}][Puzzle ${description.value}] $message")
}
