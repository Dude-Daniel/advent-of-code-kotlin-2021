package de.devdudes.aoc.helpers

// creator functions

fun <T> emptyGrid(): Grid2D<T> = Grid2D(emptyList())
fun <T> List<List<T>>.toGrid(): Grid2D<T> = Grid2D(this)

fun <T> Grid2D(values: List<List<T>>): Grid2D<T> = Grid2DImpl(values)
fun <T> MutableGrid2D(values: List<List<T>>): MutableGrid2D<T> = MutableGrid2DImpl(values.toMutableNestedList())

// mapping functions

inline fun <T, R> Grid2D<T>.mapValues(transform: (T) -> R): Grid2D<R> =
    Grid2D(values = getRawValues().mapAll(transform))

fun <T> Grid2D<T>.toMutableGrid(): MutableGrid2D<T> =
    MutableGrid2D(values = getRawValues().toMutableNestedList())

// Grid API interfaces and classes

interface Grid2D<out T> : Collection<T> {

    fun getRawValues(): List<List<T>>

    /**
     * The total number of columns.
     */
    val columns: Int

    /**
     * The total number of rows.
     */
    val rows: Int

    /**
     * The last point (max x/y) in the grid or null if the grid is empty.
     */
    val lastPoint: Point?

    operator fun get(point: Point): T
    fun getOrNull(point: Point): T?
}

interface MutableGrid2D<T> : Grid2D<T> {
    operator fun set(position: Point, element: T): T
}

abstract class Grid2DBase<T> : Grid2D<T> {

    // Collection members
    override val size: Int by lazy { if (isEmpty()) 0 else getRawValues().first().size * getRawValues().size }
    override fun contains(element: T): Boolean = getRawValues().any { row -> row.any { it == element } }
    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }
    override fun isEmpty(): Boolean = getRawValues().isEmpty() || getRawValues().first().isEmpty()
    override fun iterator(): Iterator<T> = Grid2DIterator(this)

    // own members
    override val columns: Int by lazy { getRawValues().firstOrNull()?.size ?: 0 }
    override val rows: Int by lazy { getRawValues().size }

    override val lastPoint: Point? by lazy {
        if (isEmpty()) null else Point(x = getRawValues().first().size - 1, y = getRawValues().size - 1)
    }

    override operator fun get(point: Point): T = getRawValues()[point.y][point.x]
    override fun getOrNull(point: Point): T? = getRawValues().getOrNull(point.y)?.getOrNull(point.x)
}

data class Grid2DImpl<T>(val values: List<List<T>>) : Grid2DBase<T>() {
    override fun getRawValues(): List<List<T>> = values
}

data class MutableGrid2DImpl<T>(val values: MutableList<MutableList<T>>) : Grid2DBase<T>(), MutableGrid2D<T> {
    override fun getRawValues(): List<List<T>> = values

    override fun set(position: Point, element: T): T {
        val old = values[position.y][position.x]
        values[position.y][position.x] = element
        return old
    }
}

private class Grid2DIterator<T>(private val grid2D: Grid2D<T>) : Iterator<T> {

    private var next: Point = Point(x = 0, y = 0)

    override fun hasNext(): Boolean = grid2D.getOrNull(next) != null

    override fun next(): T {
        val value = grid2D.getOrNull(next) ?: throw NoSuchElementException()

        next = if (next.x < grid2D.columns - 1) {
            // move to next in current row
            next.copy(x = next.x + 1)
        } else {
            // move to first in next row
            next.copy(x = 0, y = next.y + 1)
        }
        return value
    }
}

