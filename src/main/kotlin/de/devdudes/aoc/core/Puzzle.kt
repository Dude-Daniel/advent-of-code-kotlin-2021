package de.devdudes.aoc.core

import java.io.File

/**
 * A puzzle which needs to be solved.
 *
 * @param description puzzle description.
 * @param input input data that needs to be solved.
 * @param testInput test input data for validating correctness of [solution].
 * @param expectedTestResult expected result for executing [solution] with [testInput].
 * @param solution the implementation of the solution for solving the puzzle.
 */
class Puzzle(
    val description: Description,
    val input: String,
    val testInput: String,
    val expectedTestResult: Any,
    val solution: (List<String>) -> Any,
) {
    fun test(): Any = solution(lines(testInput))
    fun solve(): Any = solution(lines(input))

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun lines(fileName: String): List<String> {
        val uri = javaClass.classLoader.getResource("$fileName.txt").toURI()
        return File(uri).readLines()
    }
}
