package de.devdudes.aoc.helpers

import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.log10

/**
 * Calculate the greatest common divisor.
 */
fun gcd(a: Long, b: Long): Long {
    if (b == 0L) return a
    return gcd(b, a % b)
}

/**
 * Calculate the least common multiple.
 */
fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

/**
 * Concatenates two numbers. i.e. 12 and 345 becomes 12345.
 */
infix fun Int.concat(other: Int): Int {
    var aScale = 1
    while (aScale <= other) {
        aScale *= 10
    }
    return this * aScale + other
}

/**
 * Concatenates two numbers. i.e. 12 and 345 becomes 12345.
 */
infix fun Long.concat(other: Long): Long {
    var aScale = 1L
    while (aScale <= other) {
        aScale *= 10L
    }
    return this * aScale + other
}

/**
 * Returns the number of digits.
 */
fun Int.length(): Int = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

/**
 * Returns the number of digits.
 */
fun Long.length(): Int = when (this) {
    0L -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

/**
 * Returns true if the number is even.
 */
fun Int.isEven(): Boolean = this % 2 == 0

/**
 * Returns true if the number is odd.
 */
fun Int.isOdd(): Boolean = this % 2 == 1

/**
 * Returns true if the number is odd.
 */
fun Float.isWholeNumber(): Boolean {
    val asLong = this.toLong()
    return this == asLong.toFloat()
}

/**
 * Returns true if the number is odd.
 */
fun BigDecimal.isWholeNumber(): Boolean = stripTrailingZeros().scale() <= 0
