package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.mapAll
import de.devdudes.aoc.helpers.toMutableNestedList

class Day16 : Day(
    description = 16 - "The Floor Will Be Lava - Number of energized tiles",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Beam starting top left heading right",
            input = "day16",
            testInput = "day16_test",
            expectedTestResult = 46,
            solutionResult = 7_185,
            solution = { input ->
                parseContraption(input)
                    .energize(
                        startPoint = Point(x = 0, y = 0),
                        startDirection = BeamDirection.RIGHT
                    ).energizedTileCount
            }
        )

        puzzle(
            description = 2 - "Beam starting at any edge",
            input = "day16",
            testInput = "day16_test",
            expectedTestResult = 51,
            solutionResult = 7_616,
            solution = { input ->
                parseContraption(input)
                    .maximumEnergizedContraption()
                    .energizedTileCount
            }
        )
    }
)

private fun parseContraption(input: List<String>): Contraption =
    input.map { row ->
        row.toCharArray()
            .map(Char::toContraptionTile)
    }.let(::Contraption)

private data class Contraption(val grid: List<List<ContraptionTile>>) {

    fun maximumEnergizedContraption(): SolvedContraption {
        val rows = grid.size
        val columns = grid.first().size

        var result = SolvedContraption(emptyList())

        // energize all left edge tiles to right direction
        for (y in 0 until rows) {
            val current = energize(startPoint = Point(x = 0, y = y), startDirection = BeamDirection.RIGHT)
            if (current.energizedTileCount > result.energizedTileCount) result = current
        }

        // energize all right edge tiles to left direction
        for (y in 0 until rows) {
            val current = energize(startPoint = Point(x = columns - 1, y = y), startDirection = BeamDirection.LEFT)
            if (current.energizedTileCount > result.energizedTileCount) result = current
        }

        // energize all top edge tiles to bottom direction
        for (x in 0 until columns) {
            val current = energize(startPoint = Point(x = x, y = 0), startDirection = BeamDirection.BOTTOM)
            if (current.energizedTileCount > result.energizedTileCount) result = current
        }

        // energize all bottom edge tiles to top direction
        for (x in 0 until columns) {
            val current = energize(startPoint = Point(x = x, y = rows - 1), startDirection = BeamDirection.TOP)
            if (current.energizedTileCount > result.energizedTileCount) result = current
        }

        return result
    }

    fun energize(startPoint: Point, startDirection: BeamDirection): SolvedContraption {
        val tiles = grid.mapAll { SolvedContraptionTile(tile = it, energizedDirections = emptySet()) }
            .toMutableNestedList()

        emitBeam(
            tiles = tiles,
            direction = startDirection,
            position = startPoint,
        )

        return SolvedContraption(tiles)
    }

    private fun emitBeam(
        tiles: MutableList<MutableList<SolvedContraptionTile>>,
        direction: BeamDirection,
        position: Point,
    ) {
        var currentPosition = position
        var currentDirection = direction

        while (true) {
            val currentTile = tiles.getOrNull(currentPosition.y)?.getOrNull(currentPosition.x)

            if (currentTile == null || currentTile.isEnergized(currentDirection)) {
                break
            }

            tiles[currentPosition.y][currentPosition.x] = currentTile.energize(currentDirection)

            val newDirections = currentTile.tile.passBeam(currentDirection)
            if (newDirections.size == 1) {
                // continue in loop
                currentDirection = newDirections.first()
                currentPosition = currentPosition.nextPosition(currentDirection)
            } else {
                // emit beams in each direction
                newDirections.forEach { newDirection ->
                    emitBeam(
                        tiles = tiles,
                        direction = newDirection,
                        position = currentPosition.nextPosition(newDirection),
                    )
                }
                break
            }
        }
    }
}

private fun Point.nextPosition(direction: BeamDirection) =
    when (direction) {
        BeamDirection.TOP -> copy(y = y - 1)
        BeamDirection.BOTTOM -> copy(y = y + 1)
        BeamDirection.LEFT -> copy(x = x - 1)
        BeamDirection.RIGHT -> copy(x = x + 1)
    }


private data class SolvedContraption(val grid: List<List<SolvedContraptionTile>>) {

    val energizedTileCount: Int by lazy {
        grid.sumOf { row ->
            row.count { it.isEnergized }
        }
    }
}

private data class SolvedContraptionTile(
    val tile: ContraptionTile,
    val energizedDirections: Set<BeamDirection>,
) {

    val isEnergized: Boolean = energizedDirections.isNotEmpty()

    fun isEnergized(direction: BeamDirection): Boolean = energizedDirections.contains(direction)

    fun energize(direction: BeamDirection): SolvedContraptionTile =
        copy(energizedDirections = energizedDirections + direction)
}

private fun Char.toContraptionTile() = ContraptionTile.entries.first { it.char == this }

private enum class ContraptionTile(val char: Char) {
    EMPTY_SPACE('.'),
    MIRROR_LEFT_DOWN('\\'),
    MIRROR_LEFT_UP('/'),
    SPLITTER_HORIZONTAL('-'),
    SPLITTER_VERTICAL('|');

    /**
     * Passes the beam through the current tile and returns the resulting beams.
     */
    fun passBeam(direction: BeamDirection): List<BeamDirection> =
        when (this) {
            EMPTY_SPACE -> listOf(direction)
            MIRROR_LEFT_DOWN -> listOf(direction.mirrorLeftDown())
            MIRROR_LEFT_UP -> listOf(direction.mirrorLeftUp())
            SPLITTER_HORIZONTAL -> {
                if (direction.horizontal) listOf(direction) else direction.split()
            }

            SPLITTER_VERTICAL -> {
                if (direction.vertical) listOf(direction) else direction.split()
            }
        }
}

private enum class BeamDirection {
    TOP, BOTTOM, LEFT, RIGHT;

    val vertical: Boolean by lazy { this == TOP || this == BOTTOM }
    val horizontal: Boolean by lazy { this == LEFT || this == RIGHT }

    fun mirrorLeftDown(): BeamDirection =
        when (this) {
            TOP -> LEFT
            BOTTOM -> RIGHT
            LEFT -> TOP
            RIGHT -> BOTTOM
        }

    fun mirrorLeftUp(): BeamDirection =
        when (this) {
            TOP -> RIGHT
            BOTTOM -> LEFT
            LEFT -> BOTTOM
            RIGHT -> TOP
        }

    fun split(): List<BeamDirection> =
        when (this) {
            TOP,
            BOTTOM,
            -> listOf(LEFT, RIGHT)

            LEFT,
            RIGHT,
            -> listOf(TOP, BOTTOM)
        }
}
