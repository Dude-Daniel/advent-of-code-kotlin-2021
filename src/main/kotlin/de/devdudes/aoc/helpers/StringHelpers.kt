package de.devdudes.aoc.helpers

import java.math.BigInteger
import java.security.MessageDigest


/**
 * Converts string to md5 hash.
 */
fun String.md5(): String {
    val messageDigest = MessageDigest.getInstance("MD5")
    val hash = messageDigest.digest(toByteArray())
    // convert to string representation with leading zeros
    return BigInteger(1, hash).toString(16).padStart(32, '0')
}
