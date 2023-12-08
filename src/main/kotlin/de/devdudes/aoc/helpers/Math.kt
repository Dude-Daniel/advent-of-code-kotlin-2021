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
