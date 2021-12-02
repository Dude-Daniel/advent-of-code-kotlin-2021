package de.devdudes.aoc.core

/**
 * A description which is defined by a [value] and a [name].
 */
data class Description(val value: Int, val name: String)

operator fun Int.minus(description: String): Description = Description(this, description)
