package de.devdudes.aoc.helpers

import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int)

operator fun Point.minus(other: Point): Point = Point(x = x - other.x, y = y - other.y)

fun Point.move(direction: Direction, distance: Int = 1): Point =
    when (direction) {
        Direction.TOP -> copy(y = y - distance)
        Direction.BOTTOM -> copy(y = y + distance)
        Direction.LEFT -> copy(x = x - distance)
        Direction.RIGHT -> copy(x = x + distance)
    }

operator fun Point.rangeTo(next: Point): List<Point> =
    buildList {
        for (currentX in min(x, next.x)..max(x, next.x)) {
            for (currentY in min(y, next.y)..max(y, next.y)) {
                add(Point(x = currentX, y = currentY))
            }
        }
    }
