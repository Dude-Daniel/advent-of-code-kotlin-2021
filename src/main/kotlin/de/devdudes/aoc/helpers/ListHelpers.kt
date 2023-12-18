package de.devdudes.aoc.helpers

fun <T : Any> List<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> =
    this.flatMapIndexed { index: Int, item: T ->
        when {
            index == 0 || index == this.lastIndex -> listOf(index)
            predicate(item) -> listOf(index - 1, index + 1)
            else -> emptyList()
        }
    }.windowed(size = 2, step = 2) { (from, to) -> this.slice(from..to) }

inline fun <T, R> List<List<T>>.mapAll(transform: (T) -> R): List<List<R>> =
    this.map { row -> row.map { transform(it) } }

fun <T> List<List<T>>.toMutableNestedList(): MutableList<MutableList<T>> =
    this.map { row -> row.toMutableList() }.toMutableList()

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}

/**
 * Duplicates the entries specified by the [predicate].
 */
fun <T : Any> List<List<T>>.duplicateEntry(predicate: (List<T>) -> Boolean): List<List<T>> =
    map { line ->
        buildList {
            add(line)
            // add the same line again if it does not contain a galaxy
            if (predicate(line)) add(line)
        }
    }.flatten()

fun <T> List<List<T>>.printGrid(
    separator: CharSequence = "",
    map: (T) -> String = { it.toString() },
): List<List<T>> = apply {
    forEach { row ->
        row.joinToString(separator = separator) { map(it) }
            .let(::println)
    }
}

fun <T> List<List<T>>.printGridIndexed(
    separator: CharSequence = "",
    map: (Point, T) -> String,
): List<List<T>> = apply {
    forEachIndexed { y, row ->
        row.mapIndexed { x, char -> map(Point(x, y), char) }
            .joinToString(separator = separator)
            .let(::println)
    }
}
