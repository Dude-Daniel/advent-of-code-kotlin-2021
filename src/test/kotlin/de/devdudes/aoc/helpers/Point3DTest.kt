package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Point3DTest {

    @Test
    fun distanceSquaredTo() {
        assertEquals(
            expected = 0,
            actual = Point3D(1, 1, 1).distanceSquaredTo(Point3D(1, 1, 1)),
        )

        assertEquals(
            expected = 75,
            actual = Point3D(1, 1, 1).distanceSquaredTo(Point3D(6, 6, 6)),
        )

        assertEquals(
            expected = 75,
            actual = Point3D(1, 1, 1).distanceSquaredTo(Point3D(-4, -4, -4)),
        )
    }
}
