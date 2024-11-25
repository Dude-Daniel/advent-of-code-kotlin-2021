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

class DirectionParser(
    private val top: String,
    private val bottom: String,
    private val left: String,
    private val right: String,
) {

    fun parseChars(keys: List<Char>): List<Direction> = parse(keys.map(Char::toString))

    fun parse(keys: List<String>): List<Direction> = keys.map(::parse)

    fun parse(key: String): Direction =
        when (key) {
            top -> Direction.TOP
            bottom -> Direction.BOTTOM
            left -> Direction.LEFT
            right -> Direction.RIGHT
            else -> throw IllegalArgumentException("Invalid direction: $key")
        }

    companion object {
        val udlr = DirectionParser(top = "u", bottom = "d", left = "l", right = "r")
        val UDLR = DirectionParser(top = "U", bottom = "D", left = "L", right = "R")
        val tplr = DirectionParser(top = "t", bottom = "b", left = "l", right = "r")
        val TBLR = DirectionParser(top = "T", bottom = "B", left = "L", right = "R")
    }
}
