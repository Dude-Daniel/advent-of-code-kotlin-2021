package de.devdudes.aoc.helpers

enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT;

    val isHorizontal: Boolean
        get() = this == LEFT || this == RIGHT

    val isVertical: Boolean
        get() = this == TOP || this == BOTTOM

    fun invert(): Direction =
        when (this) {
            TOP -> BOTTOM
            BOTTOM -> TOP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
}
