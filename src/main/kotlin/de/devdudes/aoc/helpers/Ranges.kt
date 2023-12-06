package de.devdudes.aoc.helpers

import kotlin.math.max
import kotlin.math.min

/**
 * Splits the given range based on another range into:
 * - a matching part where the given range intersects with the other range, or null
 * - a list (between zero and two elements) with all ranges that do not exist in the other range
 */
fun LongRange.splitByRange(other: LongRange): Pair<LongRange?, List<LongRange>> {
    var matchingRange: LongRange? = null
    val uncoveredRange = mutableListOf<LongRange>()

    // start of the range is not covered by [other]
    if (first < other.first) {
        uncoveredRange.add(LongRange(start = start, endInclusive = min(last, other.first - 1)))
    }

    // end of the range is not covered by [other]
    if (last > other.last) {
        uncoveredRange.add(LongRange(start = max(first, other.last + 1), endInclusive = last))
    }

    // find matching range if this range overlaps [other] in any possible way
    val matchingFirst = max(first, other.first)
    val matchingLast = min(last, other.last)

    if (matchingFirst <= matchingLast) {
        matchingRange = LongRange(matchingFirst, matchingLast)
    }

    return matchingRange to uncoveredRange
}
