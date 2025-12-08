package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PointTest {

    @Test
    fun `neighborFour - including center element`() {
        assertEquals(
            expected = setOf(
                Point(0, 1),
                Point(1, 0),
                Point(1, 1),
                Point(1, 2),
                Point(2, 1),
            ),
            actual = Point(1, 1).neighborFour(includesCurrentPoint = true),
        )
    }

    @Test
    fun `neighborFour - excluding center element`() {
        assertEquals(
            expected = setOf(
                Point(0, 1),
                Point(1, 0),
                Point(1, 2),
                Point(2, 1),
            ),
            actual = Point(1, 1).neighborFour(includesCurrentPoint = false),
        )
    }

    @Test
    fun `neighborEight - including center element`() {
        assertEquals(
            expected = setOf(
                Point(0, 0),
                Point(0, 1),
                Point(0, 2),
                Point(1, 0),
                Point(1, 1),
                Point(1, 2),
                Point(2, 0),
                Point(2, 1),
                Point(2, 2),
            ),
            actual = Point(1, 1).neighborEight(includesCurrentPoint = true),
        )
    }

    @Test
    fun `neighborEight - excluding center element`() {
        assertEquals(
            expected = setOf(
                Point(0, 0),
                Point(0, 1),
                Point(0, 2),
                Point(1, 0),
                Point(1, 2),
                Point(2, 0),
                Point(2, 1),
                Point(2, 2),
            ),
            actual = Point(1, 1).neighborEight(includesCurrentPoint = false),
        )
    }

    @Test
    fun `surroundingPoints - zero range, no element`() {
        assertEquals(
            expected = emptySet(),
            actual = Point(1, 1).surroundingPoints(
                rangeX = 0,
                rangeY = 0,
                limitDistanceByRange = false,
                includesCurrentPoint = false,
            ),
        )
    }

    @Test
    fun `surroundingPoints - zero range, one element`() {
        assertEquals(
            expected = setOf(
                Point(1, 1),
            ),
            actual = Point(1, 1).surroundingPoints(
                rangeX = 0,
                rangeY = 0,
                limitDistanceByRange = false,
                includesCurrentPoint = true,
            ),
        )
    }

    @Test
    fun `surroundingPoints - neighbor 5x5 limit by distance`() {
        assertEquals(
            expected = setOf(
                Point(0, 2),
                Point(1, 1), Point(1, 2), Point(1, 3),
                Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3), Point(2, 4),
                Point(3, 1), Point(3, 2), Point(3, 3),
                Point(4, 2),
            ),
            actual = Point(2, 2).surroundingPoints(
                rangeX = 2,
                rangeY = 2,
                limitDistanceByRange = true,
                includesCurrentPoint = true,
            ),
        )
    }

    @Test
    fun `surroundingPoints - neighbor 5x5 no limits`() {
        assertEquals(
            expected = setOf(
                Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3), Point(0, 4),
                Point(1, 0), Point(1, 1), Point(1, 2), Point(1, 3), Point(1, 4),
                Point(2, 0), Point(2, 1), Point(2, 2), Point(2, 3), Point(2, 4),
                Point(3, 0), Point(3, 1), Point(3, 2), Point(3, 3), Point(3, 4),
                Point(4, 0), Point(4, 1), Point(4, 2), Point(4, 3), Point(4, 4),
            ),
            actual = Point(2, 2).surroundingPoints(
                rangeX = 2,
                rangeY = 2,
                limitDistanceByRange = false,
                includesCurrentPoint = true,
            ),
        )
    }
}
