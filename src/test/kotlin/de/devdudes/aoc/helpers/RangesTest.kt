package de.devdudes.aoc.helpers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class RangesTest {

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
}

private fun assertRanges(
    expected: Pair<LongRange?, List<LongRange>>,
    actual: Pair<LongRange?, List<LongRange>>,
) {
    assertAll(
        { assertEquals(expected.first, actual.first, "wrong matches range") },
        { assertEquals(expected.second, actual.second, "wrong uncovered ranges") },
    )
}
