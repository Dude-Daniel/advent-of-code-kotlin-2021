package de.devdudes.aoc.core

@DslMarker
annotation class DayDslMarker

@DayDslMarker
class DayDsl {

    private val puzzles = mutableListOf<Puzzle>()

    fun puzzle(
        description: Description,
        input: String,
        testInput: String,
        expectedTestResult: Any,
        solutionResult: Any = Unit,
        solution: (List<String>) -> Any,
    ) {
        puzzles.add(
            Puzzle(
                description = description,
                input = input,
                testInput = testInput,
                expectedTestResult = expectedTestResult,
                solutionResult = solutionResult,
                solution = solution,
            )
        )
    }

    fun build(): List<Puzzle> = puzzles
}
