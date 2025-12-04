package de.devdudes.aoc.helpers

// factory functions

fun <T> emptyGrid(): Grid2D<T> = Grid2D(emptyList())
fun <T> emptyMutableGrid(): MutableGrid2D<T> = MutableGrid2D(emptyList())

fun <T> List<List<T>>.toGrid(): Grid2D<T> = Grid2D(this)
fun <T> List<List<T>>.toMutableGrid(): MutableGrid2D<T> = MutableGrid2D(this)

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

// subset functions

/**
 * Returns the 4 neighbor elements as a new grid. All values that are not part of the 4 neighborhood will be set to [emptyValue].
 * The resulting grid may be smaller then the given size in case the area overlaps the current grid.
 */
fun <T> Grid2D<T>.neighborFour(center: Point, emptyValue: T, containsCenter: Boolean = false): Grid2D<T> =
    subGrid(
        center = center,
        rangeX = 1,
        rangeY = 1,
    ).let { grid ->
        val targetCenter = Point(
            x = if (center.x == 0) 0 else 1,
            y = if (center.y == 0) 0 else 1,
        )
        grid.edit {
            replace(targetCenter.moveTopLeft(1), emptyValue)
            replace(targetCenter.moveTopRight(1), emptyValue)
            if (!containsCenter) replace(targetCenter, emptyValue)
            replace(targetCenter.moveBottomLeft(1), emptyValue)
            replace(targetCenter.moveBottomRight(1), emptyValue)
        }
    }

/**
 * Returns the 8 neighbor elements as a new grid. The value of the [center] point will be set to [emptyValue].
 * The resulting grid may be smaller then the given size in case the area overlaps the current grid.
 */
fun <T> Grid2D<T>.neighborEight(center: Point, emptyValue: T, containsCenter: Boolean = false): Grid2D<T> =
    subGrid(
        center = center,
        rangeX = 1,
        rangeY = 1,
    ).let { grid ->
        val targetCenter = Point(
            x = if (center.x == 0) 0 else 1,
            y = if (center.y == 0) 0 else 1,
        )
        grid.edit {
            if (!containsCenter) replace(targetCenter, emptyValue)
        }
    }

/**
 * Returns the area around [center] with a size of [rangeX] and [rangeY] of the current grid as a new grid.
 * The resulting grid may be smaller then the given size in case the area overlaps the current grid.
 */
fun <T> Grid2D<T>.subGrid(
    center: Point,
    rangeX: Int,
    rangeY: Int,
): Grid2D<T> {
    val xRange = (center.x - rangeX).coerceAtLeast(0)..(center.x + rangeX).coerceAtMost(columns - 1)
    val yRange = (center.y - rangeY).coerceAtLeast(0)..(center.y + rangeY).coerceAtMost(rows - 1)

    return yRange.map { y ->
        xRange.map { x ->
            get(Point(x = x, y = y))
        }
    }.toGrid()
}

// mapping functions

inline fun <T, R> Grid2D<T>.mapValues(transform: (T) -> R): Grid2D<R> =
    Grid2D(values = getRawValues().mapAll(transform))

inline fun <T, R> Grid2D<T>.mapValuesIndexed(transform: (index: Point, T) -> R): Grid2D<R> =
    Grid2D(values = getRawValues().mapAllIndexed(transform))

inline fun <T, R> Grid2D<T>.mapValuesIndexedNotNull(transform: (index: Point, T) -> R?): List<R> {
    val result = mutableListOf<R>()
    for (x in 0 until columns) {
        for (y in 0 until rows) {
            val point = Point(x = x, y = y)
            val value = transform(point, get(point))
            if (value != null) result.add(value)
        }
    }
    return result
}

fun <T> Grid2D<T>.toMutableGrid(): MutableGrid2D<T> =
    MutableGrid2D(values = getRawValues().toMutableNestedList())

fun <T> Grid2D<T>.edit(block: MutableGrid2D<T>.() -> Unit): Grid2D<T> =
    toMutableGrid().let {
        it.block()
        Grid2D(it.getRawValues())
    }

fun <T> Grid2D<T>.transpose(): Grid2D<T> = Grid2D(getRawValues().transpose())

// find / search functions

fun <T> Grid2D<T>.positionOf(predicate: (T) -> Boolean): Point? {
    for (x in 0 until columns) {
        for (y in 0 until rows) {
            val point = Point(x = x, y = y)
            if (predicate(get(point))) return point
        }
    }
    return null
}

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

// delta functions

fun <T> Grid2D<T>.subtractMatching(other: Grid2D<T>, default: T): Grid2D<T> {
    if (this.rows != other.rows) error("sizes of both grids are different")
    if (this.columns != other.columns) error("sizes of both grids are different")

    return mapValuesIndexed { point, value ->
        val otherValue = other[point]
        when (value) {
            otherValue -> default
            else -> value
        }
    }
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
    fun replace(position: Point, element: T): T?
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

    override fun replace(position: Point, element: T): T? {
        val old = getOrNull(position)
        if (contains(position)) {
            values[position.y][position.x] = element
        }
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
