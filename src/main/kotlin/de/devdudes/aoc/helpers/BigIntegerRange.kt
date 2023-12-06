package de.devdudes.aoc.helpers

import java.math.BigInteger


operator fun BigInteger.rangeTo(other: BigInteger) =
    BigIntegerRange(this, other)

infix fun BigInteger.until(to: BigInteger): BigIntegerRange {
    return this..(to - 1.toBigInteger())
}

class BigIntegerRange(
    override val start: BigInteger,
    override val endInclusive: BigInteger,
) : ClosedRange<BigInteger>, Iterable<BigInteger> {
    override operator fun iterator(): Iterator<BigInteger> =
        BigIntegerRangeIterator(start, endInclusive, BigInteger.ONE)
}

fun BigIntegerRange.reversed(): Iterator<BigInteger> {
    return BigIntegerRangeIterator(endInclusive, start, -BigInteger.ONE)
}

class BigIntegerRangeIterator(
    first: BigInteger,
    last: BigInteger,
    private val step: BigInteger,
) : Iterator<BigInteger> {

    private val finalElement: BigInteger = last
    private var hasNext: Boolean = if (step > BigInteger.ZERO) first <= last else first >= last
    private var next: BigInteger = if (hasNext) first else finalElement

    override fun hasNext(): Boolean = hasNext

    override fun next(): BigInteger {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        } else {
            next += step
        }
        return value
    }
}
