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
 * Returns the permutations of this list. i.e.
 * ```
 * List: A,B,C
 * Permutations: A,B,C
 *               A,C,B
 *               B,A,C
 *               B,C,A
 *               C,B,A
 *               C,A,B
 * ```
 */
fun <T : Any> List<T>.permutations(): List<List<T>> {
    fun MutableList<List<T>>.addPermutationsRecursive(input: MutableList<T>, index: Int) {
        if (index == input.lastIndex) {
            add(input.toList())
        }

        for (i in index..input.lastIndex) {
            input.swap(from = index, to = i)
            addPermutationsRecursive(input, index + 1)
            input.swap(from = i, to = index)
        }
    }

    val solutions = mutableListOf<List<T>>()
    solutions.addPermutationsRecursive(this.toMutableList(), 0)
    return solutions
}


/**
 * Returns all possible subLists of size [size].
 */
fun <T : Any> Iterable<T>.combinations(size: Int = 2): List<List<T>> {
    val items = this as? List<T> ?: toList()
    val itemCount = items.size

    if (size > itemCount) {
        return emptyList()
    }

    /**
     * For obtaining all combinations of arbitrary size a list of indices is used. Each of these indices act as a pointer pointing to one element (its index)
     * in the list. Pointers are moved forward by one step. When the end is reached they are reset to the next position + 1. This results in the following
     * pattern (example for 3 pointers):
     * ```
     *            Items: 1,2,3,4,5
     * Pointer Movement: ^ ^ ^      [1,2,3]
     *                   ^ ^   ^    [1,2,4]
     *                   ^ ^     ^  [1,2,5]
     *                   ^   ^ ^    [1,3,4]
     *                   ^   ^   ^  [1,3,5]
     *                   ^     ^ ^  [1,4,5]
     *                     ^ ^ ^    [2,3,4]
     *                     ^ ^   ^  [2,3,5]
     *                     ^   ^ ^  [2,4,5]
     *                       ^ ^ ^  [3,4,5]
     * ```
     */
    fun MutableList<List<T>>.iterateIndex(indices: IntArray, index: Int, startOffset: Int) {
        val lastItemIndexToCoverByCurrentIndex = itemCount - size + index
        val currentIndexStart = index + startOffset

        for (currentIndex in currentIndexStart..lastItemIndexToCoverByCurrentIndex) {
            indices[index] = currentIndex

            if (index == size - 1) {
                // last element in indices
                add(indices.map { items[it] })
            } else {
                iterateIndex(
                    indices = indices,
                    index = index + 1,
                    startOffset = startOffset + currentIndex - currentIndexStart,
                )
            }
        }
        indices[index] = currentIndexStart
    }

    return buildList {
        val indices = (0 until size).toList().toIntArray()
        iterateIndex(indices = indices, index = 0, startOffset = 0)
    }
}

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

fun <T : Any> MutableList<T>.swap(from: Int, to: Int) {
    this[from] = this.set(to, this[from])
}

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
