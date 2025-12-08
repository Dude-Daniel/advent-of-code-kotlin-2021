package de.devdudes.aoc.aoc2025.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.mapValuesIndexed
import de.devdudes.aoc.helpers.moveBottom
import de.devdudes.aoc.helpers.moveLeft
import de.devdudes.aoc.helpers.moveRight
import de.devdudes.aoc.helpers.moveTop
import de.devdudes.aoc.helpers.positionOf
import de.devdudes.aoc.helpers.print
import de.devdudes.aoc.helpers.toGrid
import de.devdudes.aoc.helpers.toMutableGrid

class Day07 : Day(
    description = 7 - "Laboratories",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Number of Beam Splits",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 21,
            solutionResult = 1656,
            solution = { input ->
                TachyonGrid.from(input).calculateNumberOfBeamSplits()
            }
        )

        puzzle(
            description = 2 - "Number of Beam Paths",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 40L,
            solutionResult = 76_624_086_587_804L,
            solution = { input ->
                TachyonGrid.from(input).calculateTotalBeamPathCount()
            }
        )
    }
)

private sealed class TachyonManifoldTile {
    data object Empty : TachyonManifoldTile()
    data object Start : TachyonManifoldTile()
    data object Beam : TachyonManifoldTile()
    data object Splitter : TachyonManifoldTile()

    fun canContainBeam() = this == Empty || this == Beam

    companion object {
        fun parse(input: String): TachyonManifoldTile =
            when (input) {
                "." -> Empty
                "S" -> Start
                "|" -> Beam
                "^" -> Splitter
                else -> error("Unknown tile: $input")
            }
    }
}

private class TachyonGrid(private val grid: Grid2D<TachyonManifoldTile>) {
    private val startPoint = grid.positionOf { it == TachyonManifoldTile.Start } ?: error("Start not found")

    private fun Grid2D<TachyonManifoldTile>.printGrid() {
        print {
            when (it) {
                TachyonManifoldTile.Empty -> "."
                TachyonManifoldTile.Start -> "S"
                TachyonManifoldTile.Beam -> "|"
                TachyonManifoldTile.Splitter -> "^"
            }
        }
    }

    fun calculateNumberOfBeamSplits(): Int {
        val mutableGrid = grid.toMutableGrid()
        calculateBeamPath(mutableGrid, startPoint).also { mutableGrid.printGrid() }

        // find all places where a beam enters a splitter from the top
        return mutableGrid.mapValuesIndexed { point, tile ->
            val topTile = mutableGrid.getOrNull(point.moveTop())
            when {
                tile == TachyonManifoldTile.Splitter && topTile == TachyonManifoldTile.Beam -> 1
                else -> 0
            }
        }.sum()
    }

    fun calculateTotalBeamPathCount(): Long = calculateBeamPath(grid.toMutableGrid(), startPoint)

    private var cache = mutableMapOf<Point, Long>()

    /**
     * Solves the [grid] by filling in all beams. Returns the number of all possible ways a beam can take.
     */
    private fun calculateBeamPath(grid: MutableGrid2D<TachyonManifoldTile>, beamPoint: Point): Long {
        var point = beamPoint
        while (true) {
            val nextPoint = point.moveBottom()
            val nextTile = grid.getOrNull(nextPoint)
            when {
                nextTile == null -> return 1 // end reached

                nextTile.canContainBeam() -> {
                    // beam continues
                    grid.replace(nextPoint, TachyonManifoldTile.Beam)
                    point = nextPoint
                }

                nextTile == TachyonManifoldTile.Splitter -> {
                    fun calculateBeamPathIfNeeded(point: Point): Long =
                        if (grid.getOrNull(point)?.canContainBeam() ?: false) {
                            if (cache.containsKey(point)) {
                                cache.getValue(point)
                            } else {
                                grid.replace(point, TachyonManifoldTile.Beam)
                                calculateBeamPath(grid, point).also { cache[point] = it }
                            }
                        } else 1L

                    // split beam
                    val leftPathCount = calculateBeamPathIfNeeded(nextPoint.moveLeft())
                    val rightPathCount = calculateBeamPathIfNeeded(nextPoint.moveRight())
                    return leftPathCount + rightPathCount
                }
            }
        }
    }

    companion object {
        fun from(input: List<String>): TachyonGrid =
            TachyonGrid(
                grid = input.map { line ->
                    line.toCharArray().map { TachyonManifoldTile.parse(it.toString()) }
                }.toGrid()
            )
    }
}
