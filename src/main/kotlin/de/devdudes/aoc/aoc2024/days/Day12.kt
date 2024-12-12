package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.mapValuesIndexed
import de.devdudes.aoc.helpers.moveBottom
import de.devdudes.aoc.helpers.moveLeft
import de.devdudes.aoc.helpers.moveRight
import de.devdudes.aoc.helpers.moveTop
import de.devdudes.aoc.helpers.print
import de.devdudes.aoc.helpers.toGrid

class Day12 : Day(
    description = 12 - "Garden Groups - Fence Price",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Calculate Price based on Perimeter",
            input = "day12",
            testInput = "day12_test",
            expectedTestResult = 1_930,
            solutionResult = 1_363_484,
            solution = { input ->
                parseGarden(input).calculateFencePriceByPerimeter()
            }
        )

        puzzle(
            description = 2 - "Calculate Price based on Sides",
            input = "day12",
            testInput = "day12_test",
            expectedTestResult = 1_206,
            solutionResult = 838_988,
            solution = { input ->
                parseGarden(input).calculateFencePriceBySides()
            }
        )
    }
)

private fun parseGarden(input: List<String>): Garden =
    input.map { line -> line.map { it.toString() } }
        .toGrid()
        .mapValuesIndexed { point, label ->
            GardenTile(
                label = label,
                position = point,
                plot = null,
            )
        }.let(::Garden)

private data class GardenTile(
    val label: String,
    val position: Point,
    var plot: GardenPlot?,
    var perimeter: Int = -1,
)

private class GardenPlot(
    val label: String,
    val tiles: MutableList<GardenTile> = mutableListOf(),
    var perimeter: Int = 0,
)

private class Garden(private val tiles: Grid2D<GardenTile>) {

    fun calculateFencePriceByPerimeter(): Int {
        val plots = findPlots()
        tiles.print(plots)
        return plots.sumOf { it.tiles.size * it.perimeter }
    }

    fun calculateFencePriceBySides(): Int {
        val plots = findPlots()
        tiles.print(plots)

        return plots.sumOf { plot ->

            // collect all borders between to tiles with different label.
            val plotBorders = plot.tiles.flatMap { tile ->
                buildList {
                    val topPosition = tile.position.moveTop()
                    val topTile = tiles.getOrNull(topPosition)
                    if (topTile == null || topTile.label != tile.label) add(topPosition to tile.position)

                    val bottomPosition = tile.position.moveBottom()
                    val bottomTile = tiles.getOrNull(bottomPosition)
                    if (bottomTile == null || bottomTile.label != tile.label) add(tile.position to bottomPosition)

                    val leftPosition = tile.position.moveLeft()
                    val leftTile = tiles.getOrNull(leftPosition)
                    if (leftTile == null || leftTile.label != tile.label) add(leftPosition to tile.position)

                    val rightPosition = tile.position.moveRight()
                    val rightTile = tiles.getOrNull(rightPosition)
                    if (rightTile == null || rightTile.label != tile.label) add(tile.position to rightPosition)
                }
            }.toSet() // convert to set to remove duplicate entries

            // take all horizontal borders from plotBorders
            val rowBorders = plotBorders
                .filter { (from, to) -> from.y != to.y } // ignore all borders which are vertical
                .groupBy(keySelector = { it.first.y }, valueTransform = { it.first.x }) // group by row

            // count horizontal borders
            val rowBorderCount = rowBorders.map { (y, borders) ->
                val borderCount = borders
                    .sortedBy { it }
                    .zipWithNext()
                    .count { (prevX, nextX) ->
                        val bordersAreNextToEachOther = prevX + 1 == nextX
                        if (bordersAreNextToEachOther) {
                            // count only if the two tiles above and below differ. Then the two given borders are two separate ones.
                            val prevTile1 = tiles.getOrNull(Point(prevX, y))
                            val nextTile1 = tiles.getOrNull(Point(nextX, y))
                            val prevTile2 = tiles.getOrNull(Point(prevX, y + 1))
                            val nextTile2 = tiles.getOrNull(Point(nextX, y + 1))
                            val prevDiffer = prevTile1 != null && nextTile1 != null && prevTile1.label != nextTile1.label
                            val nextDiffer = prevTile2 != null && nextTile2 != null && prevTile2.label != nextTile2.label
                            prevDiffer && nextDiffer
                        } else true
                    }
                borderCount + 1 // add one as zipping does not count the first border
            }.sum()

            // take all vertical borders from plotBorders
            val columnBorders = plotBorders
                .filter { (from, to) -> from.x != to.x } // ignore all borders which are horizontal
                .groupBy(keySelector = { it.first.x }, valueTransform = { it.first.y }) // group by column

            // count vertical borders
            val columnBorderCount = columnBorders.map { (x, borders) ->
                val borderCount = borders
                    .sortedBy { it }
                    .zipWithNext()
                    .count { (prevY, nextY) ->
                        val bordersAreNextToEachOther = prevY + 1 == nextY
                        if (bordersAreNextToEachOther) {
                            // count only if the two tiles to the left and right differ. Then the two given borders are two separate ones.
                            val prevTile1 = tiles.getOrNull(Point(x, prevY))
                            val nextTile1 = tiles.getOrNull(Point(x, nextY))
                            val prevTile2 = tiles.getOrNull(Point(x + 1, prevY))
                            val nextTile2 = tiles.getOrNull(Point(x + 1, nextY))
                            val prevDiffer = prevTile1 != null && nextTile1 != null && prevTile1.label != nextTile1.label
                            val nextDiffer = prevTile2 != null && nextTile2 != null && prevTile2.label != nextTile2.label
                            prevDiffer && nextDiffer
                        } else true
                    }
                borderCount + 1 // add one as zipping does not count the first border
            }.sum()

            val borderCount = rowBorderCount + columnBorderCount
            plot.tiles.size * borderCount // calculate price
        }
    }

    private fun findPlots(): List<GardenPlot> {
        val plots = mutableListOf<GardenPlot>()
        tiles.forEachIndexed { point: Point, tile: GardenTile ->
            if (tile.plot == null) {
                val plot = GardenPlot(label = tile.label)
                findTilesForGardenPlot(tiles, point, plot)
                plots.add(plot)
            }
        }
        return plots
    }

    private fun findTilesForGardenPlot(tiles: Grid2D<GardenTile>, point: Point, plot: GardenPlot) {
        val tile = tiles.getOrNull(point)
        when {
            tile == null -> Unit // out of bounds
            tile.label != plot.label -> Unit // tile is not part of the current plot
            tile.plot == null -> {
                val perimeter =
                    tile.perimeterTo(tiles.getOrNull(point.moveTop())) +
                            tile.perimeterTo(tiles.getOrNull(point.moveRight())) +
                            tile.perimeterTo(tiles.getOrNull(point.moveBottom())) +
                            tile.perimeterTo(tiles.getOrNull(point.moveLeft()))

                tile.plot = plot
                tile.perimeter = perimeter
                plot.perimeter += perimeter
                plot.tiles.add(tile)

                findTilesForGardenPlot(tiles, point.moveTop(), plot)
                findTilesForGardenPlot(tiles, point.moveRight(), plot)
                findTilesForGardenPlot(tiles, point.moveBottom(), plot)
                findTilesForGardenPlot(tiles, point.moveLeft(), plot)
            }
        }
    }

    private fun GardenTile.perimeterTo(other: GardenTile?): Int = if (other?.label != label) 1 else 0

    private fun Grid2D<GardenTile>.print(plots: List<GardenPlot>) {
        val allColors = LogColor.AllColors
        val plotColors = plots.withIndex().associate { it.value to allColors[it.index % allColors.size] }

        this.print { (it.plot?.label ?: ".").colored(plotColors.getValue(it.plot!!)) }
    }
}
