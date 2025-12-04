package de.devdudes.aoc.helpers

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

data class Point(val x: Int, val y: Int)
data class Point3D(val x: Int, val y: Int, val z: Int) {
    companion object {
        val MIN = Point3D(x = Int.MIN_VALUE, y = Int.MIN_VALUE, z = Int.MIN_VALUE)
        val MAX = Point3D(x = Int.MAX_VALUE, y = Int.MAX_VALUE, z = Int.MAX_VALUE)
    }
}

operator fun Point.minus(other: Point): Point = Point(x = x - other.x, y = y - other.y)
operator fun Point.plus(other: Point): Point = Point(x = x + other.x, y = y + other.y)
operator fun Point.times(other: Int): Point = Point(x = x * other, y = y * other)

fun Point.distanceTo(other: Point): Int {
    val delta = this - other
    return delta.x.absoluteValue + delta.y.absoluteValue
}

fun Point.move(direction: Direction, distance: Int = 1): Point =
    when (direction) {
        Direction.TOP -> copy(y = y - distance)
        Direction.BOTTOM -> copy(y = y + distance)
        Direction.LEFT -> copy(x = x - distance)
        Direction.RIGHT -> copy(x = x + distance)
    }

fun Point.moveTop(distance: Int = 1): Point = move(direction = Direction.TOP, distance = distance)
fun Point.moveBottom(distance: Int = 1): Point = move(direction = Direction.BOTTOM, distance = distance)
fun Point.moveLeft(distance: Int = 1): Point = move(direction = Direction.LEFT, distance = distance)
fun Point.moveRight(distance: Int = 1): Point = move(direction = Direction.RIGHT, distance = distance)

fun Point.moveTopLeft(distance: Int = 1): Point = moveTop(distance).moveLeft(distance)
fun Point.moveTopRight(distance: Int = 1): Point = moveTop(distance).moveRight(distance)
fun Point.moveBottomLeft(distance: Int = 1): Point = moveBottom(distance).moveLeft(distance)
fun Point.moveBottomRight(distance: Int = 1): Point = moveBottom(distance).moveRight(distance)

operator fun Point.rangeTo(next: Point): List<Point> =
    buildList {
        for (currentX in min(x, next.x)..max(x, next.x)) {
            for (currentY in min(y, next.y)..max(y, next.y)) {
                add(Point(x = currentX, y = currentY))
            }
        }
    }

/**
 * Return the points around the given point in a 4 neighborhood relationship.
 */
fun Point.neighborFour(includesCurrentPoint: Boolean = false): Set<Point> =
    surroundingPoints(
        rangePerAxis = 1,
        limitDistanceByRange = true,
        includesCurrentPoint = includesCurrentPoint,
    )

/**
 * Return the points around the given point in a 8 neighborhood relationship.
 */
fun Point.neighborEight(includesCurrentPoint: Boolean = false): Set<Point> =
    surroundingPoints(
        rangePerAxis = 1,
        limitDistanceByRange = false,
        includesCurrentPoint = includesCurrentPoint,
    )

fun Point.surroundingPoints(rangePerAxis: Int, limitDistanceByRange: Boolean = true, includesCurrentPoint: Boolean = false): Set<Point> =
    surroundingPoints(
        rangeX = rangePerAxis,
        rangeY = rangePerAxis,
        limitDistanceByRange = limitDistanceByRange,
        includesCurrentPoint = includesCurrentPoint,
    )

/**
 * Returns the points around the given point within the given range [rangeX] and [rangeY] from the current point. When [limitDistanceByRange] is false
 * then the resulting points will cover a rectangle within the given range.
 *
 * If [limitDistanceByRange] is true then the maximum range (of [rangeX] and [rangeY]) is used to determine the maximum distance from the given point to
 * all the surrounding points (i.e. a max range of 3 means that any surrounding point can be reached by moving 3 steps into horizontal or vertical directions
 * starting from the center).
 */
fun Point.surroundingPoints(
    rangeX: Int,
    rangeY: Int,
    limitDistanceByRange: Boolean = false,
    includesCurrentPoint: Boolean = false,
): Set<Point> {
    val xRange = (x - rangeX)..(x + rangeX)
    val yRange = (y - rangeY)..(y + rangeY)
    val maxDistance = max(rangeX, rangeY)

    return buildSet {
        yRange.forEach { yPosition ->
            xRange.forEach { xPosition ->
                val pointToAdd = Point(xPosition, yPosition)
                val currentDistance = pointToAdd.distanceTo(this@surroundingPoints)

                val matchesDistanceCondition = !limitDistanceByRange || currentDistance <= maxDistance
                val matchesCurrentPointCondition = includesCurrentPoint || pointToAdd != this@surroundingPoints

                if (matchesDistanceCondition && matchesCurrentPointCondition) {
                    add(pointToAdd)
                }
            }
        }
    }
}
