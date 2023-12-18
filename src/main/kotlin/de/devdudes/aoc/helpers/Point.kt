package de.devdudes.aoc.helpers

data class Point(val x: Int, val y: Int)

operator fun Point.minus(other: Point): Point = Point(x = x - other.x, y = y - other.y)

fun Point.move(direction: Direction, distance: Int = 1): Point =
    when (direction) {
        Direction.TOP -> copy(y = y - distance)
        Direction.BOTTOM -> copy(y = y + distance)
        Direction.LEFT -> copy(x = x - distance)
        Direction.RIGHT -> copy(x = x + distance)
    }
