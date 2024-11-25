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

    fun others(): List<Direction> = ALL - this

    fun turnLeft(): Direction =
        when (this) {
            TOP -> LEFT
            BOTTOM -> RIGHT
            LEFT -> BOTTOM
            RIGHT -> TOP
        }

    fun turnRight(): Direction =
        when (this) {
            TOP -> RIGHT
            BOTTOM -> LEFT
            LEFT -> TOP
            RIGHT -> BOTTOM
        }

    companion object {
        val ALL = listOf(TOP, BOTTOM, LEFT, RIGHT)
    }
}
