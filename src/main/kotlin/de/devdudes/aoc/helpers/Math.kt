package de.devdudes.aoc.helpers

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
