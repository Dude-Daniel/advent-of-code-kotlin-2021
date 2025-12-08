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
 * Returns a new [Long] containing the first [count] digits of the given value. i.e. 12345 with a count of 2 returns 12.
 */
fun Long.takeDigits(count: Int): Long = "$this".take(count).toLongOrNull() ?: 0L

/**
 * Returns a new [Long] with the first [count] digits removed. i.e. 12345 with a count of 2 returns 345.
 */
fun Long.dropDigits(count: Int): Long = "$this".drop(count).toLong()

/**
 * Returns a new [Long] containing the last [count] digits of the given value. i.e. 12345 with a count of 2 returns 45.
 */
fun Long.takeLastDigits(count: Int): Long = "$this".takeLast(count).toLong()

/**
 * Returns a new [Long] with the last [count] digits removed. i.e. 12345 with a count of 2 returns 123.
 */
fun Long.dropLastDigits(count: Int): Long = "$this".dropLast(count).toLong()

/**
 * Splits the given number at the given [position]. i.e. 12 with a position of 1 returns 1 and 2.
 */
fun Long.split(position: Int): Pair<Long, Long> {
    val string = "$this"
    val left = string.take(position).toLong()
    val right = string.drop(position).toLong()
    return left to right
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

@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("productOfInt")
inline fun <T> Iterable<T>.productOf(selector: (T) -> Int): Int {
    var product = 1
    for (element in this) {
        product *= selector(element)
    }
    return product
}

@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("productOfLong")
inline fun <T> Iterable<T>.productOf(selector: (T) -> Long): Long {
    var product = 1L
    for (element in this) {
        product *= selector(element)
    }
    return product
}
