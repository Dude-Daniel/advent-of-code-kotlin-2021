package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Direction
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.forEachIndexed
import de.devdudes.aoc.helpers.move
import de.devdudes.aoc.helpers.toGrid

class Day21 : Day(
    description = 21 - "Keypad Conundrum",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Unknown",
            input = "day21",
            testInput = "day21_test",
            expectedTestResult = 126_384,
            solutionResult = 94_284,
            solution = { input ->
//                NumberPad().getKeyDirections()

                val dPad = DPad()

                val dpadCount = 2
                parseNumberKeys(input).sumOf { sequence ->
                    val inputWays = NumberPad().findInputSequences(sequence)

                    var dpadWays = inputWays
                    repeat(dpadCount) {
                        dpadWays = dpadWays.flatMap { dPad.findInputSequences(it) }
                    }

                    val size = dpadWays.minOf { it.keys.size }
                    val number = sequence.keys.joinToString("").dropLast(1).toInt()
                    println("size: $size, number: $number --> ${size * number}")
                    size * number
                }


//                parseNumberKeys(input).sumOf { sequence ->
//                    val ways = NumberPad().findInputSequences(sequence)
//                    val dWays1 = ways.flatMap {
////                    println("DPAD1: $it")
//                        DPad().findInputSequences(it)
////                        .also { println("$it") }
//                    }
//                    val dWays2 = dWays1.flatMap {
////                    println("DPAD2: $it")
//                        DPad().findInputSequences(it)
////                        .also {
////                            "$it".let {
////                                if (it.contains("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A")) println(it)
////                            }
////                        }
//                    }
//
//                    val size = dWays2.minOf { it.keys.size }
//                    val number = sequence.keys.joinToString("").dropLast(1).toInt()
//                    println("size: $size, number: $number --> ${size * number}")
//                    size * number
//                    // 68 * 29,
//                    // 60 * 980,
//                    // 68 * 179,
//                    // 64 * 456, and
//                    // 64 * 379
//                }

//                val ways = NumberPad().findInputSequences(parseNumberKeys(input).first())
//                val dWays1 = ways.flatMap {
////                    println("DPAD1: $it")
//                    DPad().findInputSequences(it)
////                        .also { println("$it") }
//                }
//                val dWays2 = dWays1.flatMap {
////                    println("DPAD2: $it")
//                    DPad().findInputSequences(it)
////                        .also {
////                            "$it".let {
////                                if (it.contains("<vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A")) println(it)
////                            }
////                        }
//                }

//                1231
//                parseNumberKeys(input).forEach {
//                    NumberPad().findInputSequences(it)
//                }
                /*
                <A^A>^^AvvvA, <A^A^>^AvvvA, and <A^A^^>AvvvA
                <A^A>^^AvvvA, <A^A^>^AvvvA, <A^A^^>AvvvA


                <A^A>^^AvvvA
                v<<A>>^A<A>AvA<^AA>A<vAAA>^A
                v<<A>>^A<A>AvA<^A



                <vA<AA>>^AvAA<^A>A<v<A>>^AvA^A<vA>^A<v<A>^A>AAvA^A<v<A>A>^AAAvA<^A>A
                v<<A>>^A<A>AvA<^AA>A<vAAA>^A
                <A^A>^^AvvvA
                029A
                 */

//                dWays2.sumOf { sequence ->
//                    sequence
//                    it.keys.first()
//                }
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day21",
            testInput = "day21_test",
            expectedTestResult = Unit,
            solutionResult = Unit,
            solution = { input ->
                // not implemented yet
                TODO()
            }
        )
    }
)

private fun parseNumberKeys(input: List<String>): List<KeySequence> =
    input.map { KeySequence(it.toCharArray().toList()) }

private data class KeySequence(val keys: List<Char>) {
    override fun toString(): String = keys.joinToString("")
}

private abstract class Keypad {
    data class Key(val char: Char, val isPressable: Boolean)

    abstract fun getKeypadGrid(): Grid2D<Key>

    private val cache : MutableMap<Pair<Char, List<Char>>, List<KeySequence>> = mutableMapOf()

    fun findInputSequences(target: KeySequence): List<KeySequence> {
        val sequences = getKeyDirections()

        fun findSubSequence(current: Char, chars: List<Char>): List<KeySequence> {
            if (cache.contains(current to chars)) return cache.getValue(current to chars)

            if (chars.isEmpty()) return emptyList()
            val next = chars.first()

            val paths = sequences.getValue(current to next)

            val subPaths = findSubSequence(next, chars.drop(1))
            return if (subPaths.isEmpty()) {
                paths
            } else {
                subPaths.flatMap { nextPath ->
                    paths.map { previousPath ->
                        KeySequence(previousPath.keys + nextPath.keys)
                    }
                }
            }.also { cache[current to chars] = it }
        }

        return findSubSequence('A', target.keys)
    }

    fun getKeyDirections(): MutableMap<Pair<Char, Char>, List<KeySequence>> {
        val keyPad = getKeypadGrid()

        val directions = mutableMapOf<Pair<Char, Char>, List<KeySequence>>()
        keyPad.forEachIndexed { fromPoint: Point, fromKey: Key ->
            if (fromKey.isPressable) {
                keyPad.findKeyDirections(fromPoint, fromKey)
                    .mapKeys { fromKey.char to it.key }
                    .mapValues { (_, values) ->
                        if (values.isEmpty()) listOf(KeySequence(listOf('A')))
                        else values.map { it.toKeySequence() }
                    }
                    .let(directions::putAll)
            }
        }
        return directions
    }

    private fun Grid2D<Key>.findKeyDirections(from: Point, fromKey: Key): Map<Char, List<List<Direction>>> {
        val results = mutableMapOf<Char, List<List<Direction>>>()
        forEachIndexed { toPoint: Point, toKey: Key ->
//            if (fromKey != toKey && toKey.isPressable) {
            if (toKey.isPressable) {
                val directions = findKeyDirections(from, toPoint).orEmpty()
                results[toKey.char] = directions
            }
        }
        return results
    }

    private fun Grid2D<Key>.findKeyDirections(from: Point, to: Point): List<List<Direction>>? {
        if (from == to) return emptyList()

        val fromKey = get(from)
        if (!fromKey.isPressable) return null

        val nextDirections = buildList {
            when {
                from.x < to.x -> add(Direction.RIGHT)
                from.x > to.x -> add(Direction.LEFT)
            }

            when {
                from.y < to.y -> add(Direction.BOTTOM)
                from.y > to.y -> add(Direction.TOP)
            }
        }

        return nextDirections.flatMap { direction ->
            val sequences = findKeyDirections(from = from.move(direction), to = to)
            when {
                sequences == null -> emptyList()
                sequences.isEmpty() -> listOf(listOf(direction))
                else -> {
                    sequences.map { directions -> listOf(direction) + directions }
                }
            }
        }
    }

    private fun List<Direction>.toKeySequence(): KeySequence =
        KeySequence(
            map { direction ->
                when (direction) {
                    Direction.TOP -> '^'
                    Direction.BOTTOM -> 'v'
                    Direction.LEFT -> '<'
                    Direction.RIGHT -> '>'
                }
            } + 'A'
        )
}

private class NumberPad : Keypad() {

    override fun getKeypadGrid(): Grid2D<Key> = listOf(
        "789",
        "456",
        "123",
        " 0A",
    ).map { row -> row.map { char -> Key(char = char, isPressable = char != ' ') } }
        .toGrid()
}

private class DPad : Keypad() {

    override fun getKeypadGrid(): Grid2D<Key> = listOf(
        " ^A",
        "<v>",
    ).map { row -> row.map { char -> Key(char = char, isPressable = char != ' ') } }
        .toGrid()
}

//private class DPad : Keypad{
//
//}
