package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RangesTest {

    @Test
    fun range() {
        assertEquals(expected = 4L, actual = (2L..5L step 1).range)
        assertEquals(expected = 3L, actual = (2L..5L step 2).range)
        assertEquals(expected = 4L, actual = (2L..5L step 3).range)
        assertEquals(expected = 1L, actual = (2L..5L step 4).range)
    }

    @Test
    fun valueCount() {
        // range with step = 1
        assertEquals(expected = 3L, actual = (2L..4L step 1).valueCount)

        // range with step = 2
        assertEquals(expected = 2L, actual = (2L..4L step 2).valueCount)
        assertEquals(expected = 2L, actual = (2L..5L step 2).valueCount)
        assertEquals(expected = 3L, actual = (2L..6L step 2).valueCount)
        assertEquals(expected = 3L, actual = (2L..7L step 2).valueCount)
        assertEquals(expected = 4L, actual = (2L..8L step 2).valueCount)
        assertEquals(expected = 4L, actual = (2L..9L step 2).valueCount)

        // range with step = 3
        assertEquals(expected = 1L, actual = (2L..4L step 3).valueCount)
        assertEquals(expected = 2L, actual = (2L..5L step 3).valueCount)
        assertEquals(expected = 2L, actual = (2L..6L step 3).valueCount)
        assertEquals(expected = 2L, actual = (2L..7L step 3).valueCount)
        assertEquals(expected = 3L, actual = (2L..8L step 3).valueCount)
        assertEquals(expected = 3L, actual = (2L..9L step 3).valueCount)
    }

    @Test
    fun overlaps() {
        // matching ranges
        assertTrue(actual = (10L..20L).overlaps(10L..20L))

        // first range includes the second one
        assertTrue(actual = (10L..20L).overlaps(12L..15L))

        // second range includes the first one
        assertTrue(actual = (12L..15L).overlaps(10L..20L))

        // first range overlaps at the start of the second one
        assertTrue(actual = (10L..20L).overlaps(15L..30L))

        // second range overlaps at the start of the first one
        assertTrue(actual = (15L..30L).overlaps(10L..20L))

        // first range is smaller then the second one
        assertFalse(actual = (10L..20L).overlaps(21L..30L))

        // first range is larger then the second one
        assertFalse(actual = (21L..30L).overlaps(10L..20L))
    }

    @Test
    fun consecutiveTo() {
        // first range is placed right before the second one
        assertTrue(actual = (10L..20L).consecutiveTo(21L..30L))

        // first range is placed right after the second one
        assertTrue(actual = (21L..30L).consecutiveTo(10L..20L))

        // first range overlaps at the start of the second one
        assertFalse(actual = (10L..20L).consecutiveTo(20L..30L))

        // first range overlaps at the end of the second one
        assertFalse(actual = (20L..30L).consecutiveTo(10L..20L))

        // first range is smaller then the second one
        assertFalse(actual = (10L..20L).consecutiveTo(22L..30L))

        // first range is larger then the second one
        assertFalse(actual = (22L..30L).consecutiveTo(10L..20L))
    }

    @Test
    fun splitByRange() {
        // matching range
        assertRanges(
            expected = (10L..20L) to emptyList(),
            actual = (10L..20L).splitByRange(10L..20L),
        )

        // source range is before other range
        assertRanges(
            expected = null to listOf(1L..5L),
            actual = (1L..5L).splitByRange(10L..20L),
        )

        // source range is after other range
        assertRanges(
            expected = null to listOf(30L..40L),
            actual = (30L..40L).splitByRange(10L..20L),
        )

        // source range overlaps other range at the start and at the end
        assertRanges(
            expected = (10L..20L) to listOf(1L..9L, 21L..30L),
            actual = (1L..30L).splitByRange(10L..20L),
        )

        // source range overlaps other range at the start
        assertRanges(
            expected = (10L..15L) to listOf(1L..9L),
            actual = (1L..15L).splitByRange(10L..20L),
        )

        // source range overlaps other range at the end
        assertRanges(
            expected = (15L..20L) to listOf(21L..30L),
            actual = (15L..30L).splitByRange(10L..20L),
        )
    }

    @Test
    fun merge() {
        // both ranges are equal
        assertRange(
            expected = 10L..20L,
            actual = (10L..20L).merge(10L..20L),
        )

        // first range overlaps the start of the second range
        assertRange(
            expected = 10L..25L,
            actual = (10L..20L).merge(15L..25L),
        )

        // first range overlaps the end of the second range
        assertRange(
            expected = 10L..25L,
            actual = (15L..25L).merge(10L..20L),
        )

        // first range is placed consecutive before the second range
        assertRange(
            expected = 10L..30L,
            actual = (10L..20L).merge(21L..30L),
        )

        // first range is placed consecutive after the second range
        assertRange(
            expected = 10L..30L,
            actual = (21L..30L).merge(10L..20L),
        )

        // first range is placed before the second range
        assertNull((10L..20L).merge(22L..30L))

        // first range is placed after the second range
        assertNull((22L..30L).merge(10L..20L))
    }
}

private fun assertRange(
    expected: LongRange?,
    actual: LongRange?,
) {
    assertEquals(expected, actual, "ranges do not match")
}

private fun assertRanges(
    expected: Pair<LongRange?, List<LongRange>>,
    actual: Pair<LongRange?, List<LongRange>>,
) {
    assertAll(
        { assertEquals(expected.first, actual.first, "matching ranges differ") },
        { assertEquals(expected.second, actual.second, "uncovered ranges differ") },
    )
}
