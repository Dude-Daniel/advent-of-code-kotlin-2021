package de.devdudes.aoc.helpers

fun <T : Any> List<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> =
    this.flatMapIndexed { index: Int, item: T ->
        when {
            index == 0 || index == this.lastIndex -> listOf(index)
            predicate(item) -> listOf(index - 1, index + 1)
            else -> emptyList()
        }
    }.windowed(size = 2, step = 2) { (from, to) -> this.slice(from..to) }

/**
 * Splits the Strings inside the given list at all positions where the given [predicate] matches all these String.
 */
fun List<String>.splitAtMatchingPositions(predicate: (Char) -> Boolean): List<List<String>> {
    val matchingIndices = indicesOfAll(predicate)
    val groupingIndices = (matchingIndices +
            (-1) + // the position before the first element (exclusive)
            this.first().length) // the position after the last element (exclusive)
        .sorted()
        .windowed(size = 2, step = 1)

    return this.map { line ->
        groupingIndices.map { (startIndex, endIndex) -> line.substring(startIndex + 1, endIndex) }
    }
}

/**
 * Returns the indices where all elements in the given list match [predicate].
 * i.e. ["A A A A", "B BBB B"] with a [predicate] checking for whitespaces returns [1, 5].
 */
fun List<String>.indicesOfAll(predicate: (Char) -> Boolean): List<Int> {
    // find indices for each entry of the given list
    val matchingIndicesForEachEntry = map { entry ->
        entry.toCharArray()
            .asList()
            .mapIndexedNotNull { index, char ->
                if (predicate(char)) index else null
            }.toSet()
    }
    // return the indices that are present in all entries
    return matchingIndicesForEachEntry.reduce { left, right -> left.intersect(right) }.sorted()
}

inline fun <T, R> List<List<T>>.mapAll(transform: (T) -> R): List<List<R>> =
    this.map { row -> row.map { transform(it) } }

inline fun <T, R> List<List<T>>.mapAllIndexed(transform: (index: Point, T) -> R): List<List<R>> =
    this.mapIndexed { y, row -> row.mapIndexed { x, value -> transform(Point(x = x, y = y), value) } }

fun <T> List<List<T>>.toMutableNestedList(): MutableList<MutableList<T>> =
    this.map { row -> row.toMutableList() }.toMutableList()

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}

/**
 * Duplicates all entries matching [predicate].
 */
fun <T : Any> List<List<T>>.duplicateEntry(predicate: (List<T>) -> Boolean): List<List<T>> =
    map { items ->
        buildList {
            add(items)
            // add the same items again if it matches the predicate
            if (predicate(items)) add(items)
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
