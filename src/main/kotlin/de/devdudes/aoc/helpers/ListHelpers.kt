package de.devdudes.aoc.helpers

fun <T : Any> List<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> =
    this.flatMapIndexed { index: Int, item: T ->
        when {
            index == 0 || index == this.lastIndex -> listOf(index)
            predicate(item) -> listOf(index - 1, index + 1)
            else -> emptyList()
        }
    }.windowed(size = 2, step = 2) { (from, to) -> this.slice(from..to) }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = (first().indices).map { mutableListOf<T>() }.toMutableList()
    forEach { list -> result.zip(list).forEach { it.first.add(it.second) } }
    return result
}
