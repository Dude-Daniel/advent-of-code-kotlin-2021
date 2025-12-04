package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

private val GRID_NINE = Grid2D(
    listOf(
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
        listOf(2, 2, 2, 2, 2, 2, 2, 2, 2),
        listOf(3, 3, 3, 3, 3, 3, 3, 3, 3),
        listOf(4, 4, 4, 4, 4, 4, 4, 4, 4),
        listOf(5, 5, 5, 5, 5, 5, 5, 5, 5),
        listOf(6, 6, 6, 6, 6, 6, 6, 6, 6),
        listOf(7, 7, 7, 7, 7, 7, 7, 7, 7),
        listOf(8, 8, 8, 8, 8, 8, 8, 8, 8),
        listOf(9, 9, 9, 9, 9, 9, 9, 9, 9),
    )
)

class Grid2DTest {

    @Test
    fun `neighborFour - full matching area`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(0, 2, 0),
                    listOf(3, 0, 3),
                    listOf(0, 4, 0),
                )
            ),
            actual = GRID_NINE.neighborFour(center = Point(2, 2), emptyValue = 0),
        )
    }

    @Test
    fun `neighborFour - overlapping area`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(0, 1),
                    listOf(2, 0),
                )
            ),
            actual = GRID_NINE.neighborFour(center = Point(0, 0), emptyValue = 0),
        )
    }

    @Test
    fun `neighborEight - full matching area`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(2, 2, 2),
                    listOf(3, 0, 3),
                    listOf(4, 4, 4),
                )
            ),
            actual = GRID_NINE.neighborEight(center = Point(2, 2), emptyValue = 0),
        )
    }

    @Test
    fun `neighborEight - overlapping area`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(0, 1),
                    listOf(2, 2),
                )
            ),
            actual = GRID_NINE.neighborEight(center = Point(0, 0), emptyValue = 0),
        )
    }

    @Test
    fun `subGrid - single value`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(3),
                )
            ),
            actual = GRID_NINE.subGrid(center = Point(2, 2), rangeX = 0, rangeY = 0),
        )
    }

    @Test
    fun `subGrid - single row`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(3, 3, 3, 3, 3),
                )
            ),
            actual = GRID_NINE.subGrid(center = Point(2, 2), rangeX = 2, rangeY = 0),
        )
    }

    @Test
    fun `subGrid - single column`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(1),
                    listOf(2),
                    listOf(3),
                    listOf(4),
                    listOf(5),
                )
            ),
            actual = GRID_NINE.subGrid(center = Point(2, 2), rangeX = 0, rangeY = 2),
        )
    }

    @Test
    fun `subGrid - overlaps top left`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(1, 1, 1, 1),
                    listOf(2, 2, 2, 2),
                    listOf(3, 3, 3, 3),
                    listOf(4, 4, 4, 4),
                )
            ),
            actual = GRID_NINE.subGrid(center = Point(1, 1), rangeX = 2, rangeY = 2),
        )
    }

    @Test
    fun `subGrid - overlaps bottom right`() {
        assertEquals(
            expected = Grid2D(
                listOf(
                    listOf(6, 6, 6, 6),
                    listOf(7, 7, 7, 7),
                    listOf(8, 8, 8, 8),
                    listOf(9, 9, 9, 9),
                )
            ),
            actual = GRID_NINE.subGrid(center = Point(7, 7), rangeX = 2, rangeY = 2),
        )
    }
}
