package de.devdudes.aoc.helpers

// creator functions

fun <T> emptyGrid(): Grid2D<T> = Grid2D(emptyList())
fun <T> List<List<T>>.toGrid(): Grid2D<T> = Grid2D(this)

fun <T> Grid2D(values: List<List<T>>): Grid2D<T> = Grid2DImpl(values)
fun <T> MutableGrid2D(values: List<List<T>>): MutableGrid2D<T> = MutableGrid2DImpl(values.toMutableNestedList())

inline fun <T> Grid2D(columns: Int, rows: Int, init: (point: Point) -> T): Grid2D<T> =
    MutableGrid2D(columns = columns, rows = rows, init = init)

inline fun <T> MutableGrid2D(columns: Int, rows: Int, init: (point: Point) -> T): MutableGrid2D<T> {
    return MutableList(rows) { y ->
        MutableList(columns) { x ->
            init(Point(x = x, y = y))
        }
    }.let(::MutableGrid2D)
}

// mapping functions

inline fun <T, R> Grid2D<T>.mapValues(transform: (T) -> R): Grid2D<R> =
    Grid2D(values = getRawValues().mapAll(transform))

inline fun <T, R> Grid2D<T>.mapValuesIndexed(transform: (index: Point, T) -> R): Grid2D<R> =
    Grid2D(values = getRawValues().mapAllIndexed(transform))

fun <T> Grid2D<T>.toMutableGrid(): MutableGrid2D<T> =
    MutableGrid2D(values = getRawValues().toMutableNestedList())

fun <T> Grid2D<T>.transpose(): Grid2D<T> = Grid2D(getRawValues().transpose())

// foreach functions

inline fun <T> Grid2D<T>.forEachIndexed(action: (point: Point, T) -> Unit) {
    for (x in 0 until columns) {
        for (y in 0 until rows) {
            val point = Point(x = x, y = y)
            action(Point(x = x, y = y), get(point))
        }
    }
}

// contains functions

fun <T> Grid2D<T>.contains(values: Collection<T>, pointAt: (index: Int) -> Point): Boolean {
    values.forEachIndexed { index, expectedValue ->
        val point = pointAt(index)
        val actualValue = this.getOrNull(point) ?: return false // point is out of range
        if (expectedValue != actualValue) return false // value does not match
    }
    return true
}

// print functions

fun <T> Grid2D<T>.print(
    separator: CharSequence = "",
    map: (T) -> String = { it.toString() },
): Grid2D<T> = apply { getRawValues().printGrid(separator = separator, map = map) }

fun <T> Grid2D<T>.printIndexed(
    separator: CharSequence = "",
    map: (Point, T) -> String,
): Grid2D<T> = apply { getRawValues().printGridIndexed(separator = separator, map = map) }

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

    /**
     * The last point (max x/y) in the grid.
     */
    val requireLastPoint: Point

    fun pointOfIndex(index: Int): Point = Point(x = index % rows, y = index / rows)

    operator fun get(point: Point): T
    fun getOrNull(point: Point): T?

    fun contains(point: Point): Boolean
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
    override val requireLastPoint: Point by lazy { lastPoint ?: throw NoSuchElementException() }

    override operator fun get(point: Point): T = getRawValues()[point.y][point.x]
    override fun getOrNull(point: Point): T? = getRawValues().getOrNull(point.y)?.getOrNull(point.x)

    override fun contains(point: Point): Boolean =
        point.x in 0 until columns && point.y in 0 until rows
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
