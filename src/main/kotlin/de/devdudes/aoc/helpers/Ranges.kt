package de.devdudes.aoc.helpers

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * Returns the value range of the given progression.
 */
inline val LongProgression.range: Long get() = (last - first).absoluteValue + 1

/**
 * Returns the number of values in the given progression.
 */
val LongProgression.valueCount: Long
    get() {
        val matchingItemsInRange = (range / step.absoluteValue)

        val rangeRemainder = range % step.absoluteValue
        val extraItemsInRange = if (rangeRemainder > 0) 1 else 0

        return matchingItemsInRange + extraItemsInRange
    }

/**
 * Returns true when both given ranges overlap by at least one value.
 */
fun LongRange.overlaps(other: LongRange): Boolean {
    val matchingFirst = max(first, other.first)
    val matchingLast = min(last, other.last)
    return matchingFirst <= matchingLast
}

/**
 * Returns true when one of the given ranges starts directly after the other range ends.
 */
fun LongRange.consecutiveTo(other: LongRange): Boolean =
    this.last + 1 == other.first || other.last + 1 == this.first

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

/**
 * Create a new [LongRange] when both the given range and [other] overlap.
 * The resulting range covers all elements defined by the given ranges.
 * When both ranges cannot be merged then null is returned.
 */
fun LongRange.merge(other: LongRange): LongRange? =
    if (this.overlaps(other) || this.consecutiveTo(other)) {
        min(this.first, other.first)..max(this.last, other.last)
    } else {
        null
    }
