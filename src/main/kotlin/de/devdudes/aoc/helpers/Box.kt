package de.devdudes.aoc.helpers

data class Box(val from: Point, val to: Point)

infix fun Point.boxTo(to: Point): Box = Box(this, to)

fun Box.intersects(line: AxisBoundLine, canIntersectOnEdge: Boolean = false): Boolean {
    val boxMinX = minOf(this.from.x, this.to.x)
    val boxMaxX = maxOf(this.from.x, this.to.x)

    val boxMinY = minOf(this.from.y, this.to.y)
    val boxMaxY = maxOf(this.from.y, this.to.y)

    val simpleLine = line.toLine()
    val lineMinX = minOf(simpleLine.from.x, simpleLine.to.x)
    val lineMaxX = maxOf(simpleLine.from.x, simpleLine.to.x)

    val lineMinY = minOf(simpleLine.from.y, simpleLine.to.y)
    val lineMaxY = maxOf(simpleLine.from.y, simpleLine.to.y)

    return if (canIntersectOnEdge) {
        val overlapsX = lineMinX <= boxMaxX && lineMaxX >= boxMinX
        val overlapsY = lineMinY <= boxMaxY && lineMaxY >= boxMinY
        overlapsX && overlapsY
    } else {
        val overlapsX = lineMinX < boxMaxX && lineMaxX > boxMinX
        val overlapsY = lineMinY < boxMaxY && lineMaxY > boxMinY
        overlapsX && overlapsY
    }
}
