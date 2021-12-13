package de.devdudes.aoc.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day13 : Day(description = 13 - "Transparent Origami", {

    puzzle(
        description = 1 - "First fold",
        input = "day13",
        testInput = "day13_test",
        expectedTestResult = 17,
        solutionResult = 751,
        solution = { input ->
            OrigamiPaper(input).foldFirst().size
        }
    )

    puzzle(
        description = 2 - "Fold all",
        input = "day13",
        testInput = "day13_test",
        expectedTestResult = "\n" + """
                             #####
                             #...#
                             #...#
                             #...#
                             #####
        """.trimIndent(), // 0
        solutionResult = "\n" + """
                             ###...##..#..#.###..#..#.#....#..#.#...
                             #..#.#..#.#..#.#..#.#.#..#....#.#..#...
                             #..#.#....####.#..#.##...#....##...#...
                             ###..#.##.#..#.###..#.#..#....#.#..#...
                             #....#..#.#..#.#.#..#.#..#....#.#..#...
                             #.....###.#..#.#..#.#..#.####.#..#.####
        """.trimIndent(), // PGHRKLKL
        solution = { input ->
            val foldedDots = OrigamiPaper(input).foldAll()

            val maxX = foldedDots.maxOf { it.x }
            val maxY = foldedDots.maxOf { it.y }
            val dotBoard: List<List<Char>> = List(maxY + 1) { y ->
                List(maxX + 1) { x -> if (foldedDots.contains(OrigamiDot(x, y))) '#' else '.' }
            }

            dotBoard.joinToString("\n", prefix = "\n") { line -> line.joinToString(separator = "") }
        }
    )
})

private sealed class FoldInstruction {
    abstract val position: Int

    data class X(override val position: Int) : FoldInstruction()
    data class Y(override val position: Int) : FoldInstruction()
}

private data class OrigamiDot(val x: Int, val y: Int)

private class OrigamiPaper(val input: List<String>) {

    private val dots: Set<OrigamiDot>
    private val foldInstructions: List<FoldInstruction>

    init {
        val splitIndex = input.indexOf("")

        dots = input.take(splitIndex)
            .map { line ->
                val (x, y) = line.split(",")
                OrigamiDot(x.toInt(), y.toInt())
            }.toSet()

        foldInstructions = input.takeLast(input.size - splitIndex - 1)
            .map { line ->
                val (orientation, index) = line.split(" ").last().split("=")
                if (orientation == "x") FoldInstruction.X(index.toInt()) else FoldInstruction.Y(index.toInt())
            }
    }

    fun foldAll(): Set<OrigamiDot> {
        var foldedDots = dots
        foldInstructions.forEach { foldedDots = fold(foldedDots, it) }
        return foldedDots
    }

    fun foldFirst(): Set<OrigamiDot> = fold(dots, foldInstructions.first())

    private fun fold(dots: Set<OrigamiDot>, instruction: FoldInstruction): Set<OrigamiDot> {
        return when (instruction) {
            is FoldInstruction.X -> {
                val lowerDots = dots.filter { it.x < instruction.position }
                val upperDots = dots.filter { it.x > instruction.position }
                    .map { dot ->
                        val delta = dot.x - instruction.position
                        dot.copy(x = instruction.position - delta)
                    }

                (lowerDots + upperDots).toSet()
            }
            is FoldInstruction.Y -> {
                val lowerDots = dots.filter { it.y < instruction.position }
                val upperDots = dots.filter { it.y > instruction.position }
                    .map { dot ->
                        val delta = dot.y - instruction.position
                        dot.copy(y = instruction.position - delta)
                    }

                (lowerDots + upperDots).toSet()
            }
        }
    }
}
