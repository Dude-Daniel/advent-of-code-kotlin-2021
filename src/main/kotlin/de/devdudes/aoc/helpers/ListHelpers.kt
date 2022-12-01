package de.devdudes.aoc.helpers

fun <T : Any> List<T>.splitWhen(predicate: (T) -> Boolean): List<List<T>> =
    this.flatMapIndexed { index: Int, item: T ->
        when {
            index == 0 || index == this.lastIndex -> listOf(index)
            predicate(item) -> listOf(index - 1, index + 1)
            else -> emptyList()
        }
    }.windowed(size = 2, step = 2) { (from, to) -> this.slice(from..to) }
