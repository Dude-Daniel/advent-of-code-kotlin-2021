package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.aoc2024.days.LargeWarehouse.LargeWarehouseTile
import de.devdudes.aoc.aoc2024.days.SmallWarehouse.SmallWarehouseTile
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.DirectionParser
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.MutableGrid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.logging.LogColor
import de.devdudes.aoc.helpers.logging.colored
import de.devdudes.aoc.helpers.mapValuesIndexed
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.moveLeft
import de.devdudes.aoc.helpers.moveRight
import de.devdudes.aoc.helpers.printIndexed
import de.devdudes.aoc.helpers.splitWhen
import de.devdudes.aoc.helpers.toGrid
import de.devdudes.aoc.helpers.toMutableGrid

class Day15 : Day(
    description = 15 - "Warehouse Woes",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Small Warehouse",
            input = "day15",
            testInput = "day15_test",
            expectedTestResult = 10_092,
            solutionResult = 1_406_628,
            solution = { input ->
                val (warehouse, moves) = parseSmallWarehouseAndMoves(input)
                warehouse.moveRobot(moves)
            }
        )

        puzzle(
            description = 2 - "Large Warehouse",
            input = "day15",
            testInput = "day15_test",
            expectedTestResult = 9_021,
            solutionResult = 1_432_781,
            solution = { input ->
                val (warehouse, moves) = parseLargeWarehouseAndMoves(input)
                warehouse.moveRobot(moves)
            }
        )
    }
)

private fun parseSmallWarehouseAndMoves(input: List<String>): Pair<SmallWarehouse, List<Direction>> {
    val (rawWarehouse, rawMoves) = input.splitWhen { it.isBlank() }

    var robotPosition = Point(-1, -1)
    val warehouse = rawWarehouse.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            when (char) {
                '#' -> SmallWarehouseTile.Wall
                'O' -> SmallWarehouseTile.Box
                '.' -> SmallWarehouseTile.Floor
                '@' -> {
                    robotPosition = Point(x, y)
                    SmallWarehouseTile.Floor
                }

                else -> error("unsupported warehouse tile: $char")
            }
        }
    }.toGrid().let { tiles -> SmallWarehouse(tiles = tiles, robotPosition = robotPosition) }

    val moves = DirectionParser.ARROWS.parseChars(
        keys = rawMoves.joinToString(separator = "").toCharArray().toList()
    )

    return warehouse to moves
}

private fun parseLargeWarehouseAndMoves(input: List<String>): Pair<LargeWarehouse, List<Direction>> {
    val (rawWarehouse, rawMoves) = input.splitWhen { it.isBlank() }

    var robotPosition = Point(-1, -1)
    val warehouse = rawWarehouse.mapIndexed { y, line ->
        buildList {
            line.forEachIndexed { x, char ->
                when (char) {
                    '#' -> {
                        add(LargeWarehouseTile.Wall)
                        add(LargeWarehouseTile.Wall)
                    }

                    'O' -> {
                        add(LargeWarehouseTile.Box.Left)
                        add(LargeWarehouseTile.Box.Right)
                    }

                    '.' -> {
                        add(LargeWarehouseTile.Floor)
                        add(LargeWarehouseTile.Floor)
                    }

                    '@' -> {
                        robotPosition = Point(x * 2, y)
                        add(LargeWarehouseTile.Floor)
                        add(LargeWarehouseTile.Floor)
                    }

                    else -> error("unsupported warehouse tile: $char")
                }
            }
        }
    }.toGrid().let { tiles -> LargeWarehouse(tiles = tiles, robotPosition = robotPosition) }

    val moves = DirectionParser.ARROWS.parseChars(
        keys = rawMoves.joinToString(separator = "").toCharArray().toList()
    )

    return warehouse to moves
}

private interface Warehouse {
    fun moveRobot(directions: List<Direction>): Int
}

private class SmallWarehouse(private val tiles: Grid2D<SmallWarehouseTile>, private val robotPosition: Point) : Warehouse {

    sealed class SmallWarehouseTile {
        data object Wall : SmallWarehouseTile()
        data object Box : SmallWarehouseTile()
        data object Floor : SmallWarehouseTile()
    }

    override fun moveRobot(directions: List<Direction>): Int {
        val mutableTiles = tiles.toMutableGrid()
        var position = robotPosition
        directions.forEach { direction ->
            val nextPosition = position.move(direction)
            if (mutableTiles.moveBoxes(direction = direction, position = nextPosition)) {
                position = nextPosition
            }
        }

        mutableTiles.printWarehouse(position)

        return mutableTiles.mapValuesIndexed { tilePosition, warehouseTile ->
            if (warehouseTile is SmallWarehouseTile.Box) tilePosition.calculateGoodsPositioningSystemCoordinate() else 0
        }.sum()
    }

    private fun Point.calculateGoodsPositioningSystemCoordinate(): Int = x + 100 * y

    private fun MutableGrid2D<SmallWarehouseTile>.moveBoxes(direction: Direction, position: Point): Boolean =
        when (get(position)) {
            SmallWarehouseTile.Box -> {
                val nextPosition = position.move(direction)
                val canMove = moveBoxes(direction = direction, position = nextPosition)
                if (canMove) {
                    set(nextPosition, SmallWarehouseTile.Box)
                    set(position, SmallWarehouseTile.Floor)
                }
                canMove
            }

            SmallWarehouseTile.Floor -> true
            SmallWarehouseTile.Wall -> false
        }

    private fun Grid2D<SmallWarehouseTile>.printWarehouse(robotPosition: Point) {
        printIndexed { point, tile ->
            when {
                point == robotPosition -> "@".colored(LogColor.Red)
                tile == SmallWarehouseTile.Box -> "▪".colored(LogColor.Green)
                tile == SmallWarehouseTile.Floor -> ".".colored(LogColor.GreyScale.Grey18)
                tile == SmallWarehouseTile.Wall -> "▒".colored(LogColor.Blue)
                else -> error("unsupported warehouse tile: $tile")
            }
        }
    }
}

private class LargeWarehouse(private val tiles: Grid2D<LargeWarehouseTile>, private val robotPosition: Point) : Warehouse {

    sealed class LargeWarehouseTile {
        data object Wall : LargeWarehouseTile()
        sealed class Box : LargeWarehouseTile() {
            data object Left : Box()
            data object Right : Box()
        }

        data object Floor : LargeWarehouseTile()
    }

    override fun moveRobot(directions: List<Direction>): Int {
        val mutableTiles = tiles.toMutableGrid()
        var position = robotPosition
        directions.forEach { direction ->
            val nextPosition = position.move(direction)
            if (mutableTiles.canMoveBoxes(direction = direction, position = nextPosition)) {
                mutableTiles.moveBoxes(direction = direction, position = nextPosition)
                position = nextPosition
            }
        }

        mutableTiles.printWarehouse(position)

        return mutableTiles.mapValuesIndexed { tilePosition, warehouseTile ->
            if (warehouseTile is LargeWarehouseTile.Box.Left) tilePosition.calculateGoodsPositioningSystemCoordinate() else 0
        }.sum()
    }

    private fun Point.calculateGoodsPositioningSystemCoordinate(): Int = x + 100 * y

    private fun Grid2D<LargeWarehouseTile>.canMoveBoxes(direction: Direction, position: Point): Boolean =
        when (val tile = get(position)) {
            is LargeWarehouseTile.Box -> {
                if (direction.isHorizontal) {
                    val nextPosition = position.move(direction, 2)
                    canMoveBoxes(direction = direction, position = nextPosition)
                } else {
                    val nextLeftPosition = if (tile is LargeWarehouseTile.Box.Left) position.move(direction) else position.move(direction).moveLeft()
                    val nextRightPosition = if (tile is LargeWarehouseTile.Box.Left) position.move(direction).moveRight() else position.move(direction)

                    // check if the whole box can move
                    canMoveBoxes(direction = direction, position = nextLeftPosition) && canMoveBoxes(direction = direction, position = nextRightPosition)
                }
            }

            LargeWarehouseTile.Floor -> true
            LargeWarehouseTile.Wall -> false
        }

    private fun MutableGrid2D<LargeWarehouseTile>.moveBoxes(direction: Direction, position: Point) {
        when (val tile = get(position)) {
            is LargeWarehouseTile.Box -> {
                if (direction.isHorizontal) {
                    val nextPosition = position.move(direction, 2)
                    moveBoxes(direction = direction, position = nextPosition)

                    // place box on next position
                    if (direction == Direction.LEFT) {
                        set(nextPosition, LargeWarehouseTile.Box.Left)
                        set(position.move(direction, 1), LargeWarehouseTile.Box.Right)
                    } else {
                        set(position.move(direction, 1), LargeWarehouseTile.Box.Left)
                        set(nextPosition, LargeWarehouseTile.Box.Right)
                    }

                    // place floor on current position
                    set(position, LargeWarehouseTile.Floor)
                    set(position.move(direction, -1), LargeWarehouseTile.Floor)
                } else {
                    val currentLeftPosition = if (tile is LargeWarehouseTile.Box.Left) position else position.moveLeft()
                    val currentRightPosition = if (tile is LargeWarehouseTile.Box.Left) position.moveRight() else position

                    val nextLeftPosition = currentLeftPosition.move(direction)
                    val nextRightPosition = currentRightPosition.move(direction)

                    moveBoxes(direction = direction, position = nextLeftPosition)
                    moveBoxes(direction = direction, position = nextRightPosition)

                    set(nextLeftPosition, LargeWarehouseTile.Box.Left)
                    set(nextRightPosition, LargeWarehouseTile.Box.Right)

                    set(currentLeftPosition, LargeWarehouseTile.Floor)
                    set(currentRightPosition, LargeWarehouseTile.Floor)
                }
            }

            LargeWarehouseTile.Floor -> Unit // nothing to move
            LargeWarehouseTile.Wall -> Unit // cannot be moved
        }
    }

    private fun Grid2D<LargeWarehouseTile>.printWarehouse(robotPosition: Point) {
        printIndexed { point, tile ->
            when {
                point == robotPosition -> "@".colored(LogColor.Red)
                tile == LargeWarehouseTile.Box.Left -> "[".colored(LogColor.Green)
                tile == LargeWarehouseTile.Box.Right -> "]".colored(LogColor.Green)
                tile == LargeWarehouseTile.Floor -> ".".colored(LogColor.GreyScale.Grey18)
                tile == LargeWarehouseTile.Wall -> "▒".colored(LogColor.Blue)
                else -> error("unsupported warehouse tile: $tile")
            }
        }
    }
}

