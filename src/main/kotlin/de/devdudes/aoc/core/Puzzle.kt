package de.devdudes.aoc.core

import java.io.File
import java.io.FileNotFoundException

/**
 * A puzzle which needs to be solved.
 *
 * @param description puzzle description.
 * @param input input data that needs to be solved.
 * @param testInput test input data for validating correctness of [solution].
 * @param expectedTestResult expected result for executing [solution] with [testInput].
 * @param solutionResult result for [input]. Once provided it will make sure the [solution] works correctly.
 * By default, no result is set, which means no evaluation is taking place (i.e. when implementing the solution).
 * @param solution the implementation of the solution for solving the puzzle.
 */
class Puzzle(
    val description: Description,
    val input: String,
    val testInput: String,
    val expectedTestResult: Any,
    val solutionResult: Any,
    val solution: PuzzleScope.(List<String>) -> Any,
) {
    fun test(resourceFolder: String): Any =
        PuzzleScope(isTest = true).solution(
            lines(
                fileName = testInput,
                resourceFolder = resourceFolder,
            )
        )

    fun solve(resourceFolder: String): Any =
        PuzzleScope(isTest = false).solution(
            lines(
                fileName = input,
                resourceFolder = resourceFolder,
            )
        )

    private fun lines(fileName: String, resourceFolder: String): List<String> {
        val resourceFile = "$resourceFolder/$fileName.txt"
        val resource = javaClass.classLoader.getResource(resourceFile)
            ?: throw FileNotFoundException("resource file not found: resources/$resourceFile")

        val uri = resource.toURI()
        return File(uri).readLines()
    }
}

data class PuzzleScope(val isTest: Boolean)
