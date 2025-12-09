package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoxTest {

    @Test
    fun boxTo() {
        assertEquals(
            expected = Box(
                from = Point(0, 1),
                to = Point(2, 3)
            ),
            actual = Point(0, 1) boxTo Point(2, 3),
        )
    }

    @Test
    fun `intersects canIntersectOnEdge=true`() {
        // box of size 10 x 10
        // - x coordinates reach from 10 to 20
        // - y coordinates reach from 50 to 60
        // - bottom right point at 20 x 60
        val box = Point(10, 50) boxTo Point(20, 60)

        fun horizontalLine(y: Int, fromX: Int, toX: Int): AxisBoundLine = Point(fromX, y) axisBoundLineTo Point(toX, y)
        fun verticalLine(x: Int, fromY: Int, toY: Int): AxisBoundLine = Point(x, fromY) axisBoundLineTo Point(x, toY)

        fun Box.intersectsIncludingEdge(line: AxisBoundLine): Boolean = this.intersects(canIntersectOnEdge = true, line = line)

        // horizontal lines

        // line is above the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 0, fromX = 0, toX = 30))) // line overlaps the whole box

        // inside the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 0, toX = 9))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 12, toX = 18))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 15, toX = 30))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 21, toX = 30))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 55, fromX = 0, toX = 30))) // line overlaps the whole box

        // below the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 90, fromX = 0, toX = 30))) // line overlaps the whole box

        // on the top edge of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 0, toX = 9))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 12, toX = 18))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 15, toX = 30))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 21, toX = 30))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 50, fromX = 0, toX = 30))) // line overlaps the whole box

        // on the bottom edge of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 0, toX = 9))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 12, toX = 18))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 15, toX = 30))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 21, toX = 30))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(horizontalLine(y = 60, fromX = 0, toX = 30))) // line overlaps the whole box

        // vertical lines

        // line is left of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 0, toY = 55))) // line overlaps the top side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 55, toY = 80))) // line is overlaps the bottom side
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 0, fromY = 0, toY = 80))) // line overlaps the whole box

        // inside the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 0, toY = 49))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 52, toY = 58))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 55, toY = 80))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 61, toY = 80))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 15, fromY = 0, toY = 80))) // line overlaps the whole box

        // right of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 55, toY = 80))) // line is overlaps the right side
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 30, fromY = 0, toY = 80))) // line overlaps the whole box

        // on the left edge of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 0, toY = 49))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 52, toY = 58))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 55, toY = 80))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 61, toY = 80))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 10, fromY = 0, toY = 80))) // line overlaps the whole box

        // on the right edge of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 0, toY = 49))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 52, toY = 58))) // line is inside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 55, toY = 80))) // line is overlaps the right side
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 61, toY = 80))) // line is outside of the box
        assertTrue(box.intersectsIncludingEdge(verticalLine(x = 20, fromY = 0, toY = 80))) // line overlaps the whole box
    }

    @Test
    fun `intersects canIntersectOnEdge=false`() {
        // box of size 10 x 10
        // - x coordinates reach from 10 to 20
        // - y coordinates reach from 50 to 60
        // - bottom right point at 20 x 60
        val box = Point(10, 50) boxTo Point(20, 60)

        fun horizontalLine(y: Int, fromX: Int, toX: Int): AxisBoundLine = Point(fromX, y) axisBoundLineTo Point(toX, y)
        fun verticalLine(x: Int, fromY: Int, toY: Int): AxisBoundLine = Point(x, fromY) axisBoundLineTo Point(x, toY)

        fun Box.intersectsExcludingEdge(line: AxisBoundLine): Boolean = this.intersects(canIntersectOnEdge = false, line = line)

        // horizontal lines

        // line is above the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 0, fromX = 0, toX = 30))) // line overlaps the whole box

        // inside the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertTrue(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertTrue(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 12, toX = 18))) // line is inside of the box
        assertTrue(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 21, toX = 30))) // line is outside of the box
        assertTrue(box.intersectsExcludingEdge(horizontalLine(y = 55, fromX = 0, toX = 30))) // line overlaps the whole box

        // below the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 90, fromX = 0, toX = 30))) // line overlaps the whole box

        // on the top edge of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 50, fromX = 0, toX = 30))) // line overlaps the whole box

        // on the bottom edge of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 0, toX = 9))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 0, toX = 10))) // line connects with the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 0, toX = 15))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 12, toX = 18))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 15, toX = 30))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 20, toX = 30))) // line connects with the right side of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 21, toX = 30))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(horizontalLine(y = 60, fromX = 0, toX = 30))) // line overlaps the whole box

        // vertical lines

        // line is left of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 0, toY = 55))) // line overlaps the top side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 55, toY = 80))) // line is overlaps the bottom side
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 0, fromY = 0, toY = 80))) // line overlaps the whole box

        // inside the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertTrue(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertTrue(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 52, toY = 58))) // line is inside of the box
        assertTrue(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 55, toY = 80))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 61, toY = 80))) // line is outside of the box
        assertTrue(box.intersectsExcludingEdge(verticalLine(x = 15, fromY = 0, toY = 80))) // line overlaps the whole box

        // right of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 55, toY = 80))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 30, fromY = 0, toY = 80))) // line overlaps the whole box

        // on the left edge of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 55, toY = 80))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 10, fromY = 0, toY = 80))) // line overlaps the whole box

        // on the right edge of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 0, toY = 49))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 0, toY = 50))) // line connects with the top side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 0, toY = 55))) // line overlaps the left side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 52, toY = 58))) // line is inside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 55, toY = 80))) // line is overlaps the right side
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 60, toY = 80))) // line connects with the bottom side of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 61, toY = 80))) // line is outside of the box
        assertFalse(box.intersectsExcludingEdge(verticalLine(x = 20, fromY = 0, toY = 80))) // line overlaps the whole box
    }
}
