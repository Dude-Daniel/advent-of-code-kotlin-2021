package de.devdudes.aoc.helpers

data class Line(val from: Point, val to: Point) {

    fun calculateIntersectionPoint(other: Line, excludeEnds: Boolean = true): Point? =
        calculateIntersectionPoint(
            a1 = this.from,
            a2 = this.to,
            b1 = other.from,
            b2 = other.to,
            excludeEnds = excludeEnds,
        )

    /**
     * Calculates the intersection point of two line segments:
     *
     * @param a1 The first endpoint of line segment a.
     * @param a2 The second endpoint of line segment a.
     * @param b1 The first endpoint of line segment b.
     * @param b2 The second endpoint of line segment b.
     * @return The intersection point, or null if the segments do not intersect.
     */
    private fun calculateIntersectionPoint(a1: Point, a2: Point, b1: Point, b2: Point, excludeEnds: Boolean): Point? {
        val s1 = (a2 - a1)
        val s2 = (b2 - b1)

        val det = (-s2.x * s1.y + s1.x * s2.y).toFloat()
        val s = (-s1.y * (a1.x - b1.x) + s1.x * (a1.y - b1.y)) / det
        val t = (s2.x * (a1.y - b1.y) - s2.y * (a1.x - b1.x)) / det

        val overlaps = if (excludeEnds) {
            (s > 0 && s < 1 && t > 0 && t < 1)
        } else {
            (s in 0f..1f && t in 0f..1f)
        }

        if (overlaps) {
            // Collision detected
            val x = a1.x + (t * s1.x)
            val y = a1.y + (t * s1.y)

            return Point(x.toInt(), y.toInt())
        }

        return null // Segments do not intersect
    }
}

infix fun Point.lineTo(to: Point): Line = Line(this, to)

infix fun Point.axisBoundLineTo(to: Point): AxisBoundLine =
    when {
        this.x == to.x -> AxisBoundLine.VerticalLine(x = this.x, fromY = this.y, toY = to.y)
        this.y == to.y -> AxisBoundLine.HorizontalLine(y = this.y, fromX = this.x, toX = to.x)
        else -> error("given points are not on the same axis: [$this, $to]")
    }

sealed class AxisBoundLine {
    data class HorizontalLine(val fromX: Int, val toX: Int, val y: Int) : AxisBoundLine()
    data class VerticalLine(val x: Int, val fromY: Int, val toY: Int) : AxisBoundLine()

    fun toLine() : Line =
        when(this){
            is HorizontalLine -> Point(fromX,y) lineTo Point(toX,y)
            is VerticalLine -> Point(x,fromY) lineTo Point(x,toY)
        }
}
