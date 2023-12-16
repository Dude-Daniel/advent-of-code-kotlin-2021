package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day12 : Day(
    description = 12 - "Hot Springs - Sum of possible Arrangement",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Regular Input",
            input = "day12",
            testInput = "day12_test",
            expectedTestResult = 21L,
            solutionResult = 7204L,
            solution = { input ->
                parseSpringConditions(input)
                    .calculateTotalMatches()
            }
        )

        puzzle(
            description = 2 - "Input unfolded five times",
            input = "day12",
            testInput = "day12_test",
            expectedTestResult = 525_152L,
            solutionResult = 1_672_318_386_674L,
            solution = { input ->
                parseSpringConditions(input)
                    .unfold(5)
                    .calculateTotalMatches()
            }
        )
    }
)

private fun parseSpringConditions(input: List<String>): SpringConditions =
    input.map {
        val (values, groupSizes) = it.split(" ")
        SpringCondition(
            values = values.toCharArray().toList(),
            groupSizes = groupSizes.split(",").map(String::toInt),
        )
    }.let(::SpringConditions)

private data class SpringConditions(val conditions: List<SpringCondition>) {

    fun calculateTotalMatches(): Long = conditions.sumOf { it.calculateMatches() }

    fun unfold(times: Int): SpringConditions =
        conditions.map { condition ->
            SpringCondition(
                values = List(times) { condition.values }.reduce { acc, chars -> acc + '?' + chars },
                groupSizes = List(times) { condition.groupSizes }.flatten(),
            )
        }.let(::SpringConditions)

}

private data class SpringCondition(
    val values: List<Char>,
    val groupSizes: List<Int>,
) {

    private val cache: MutableMap<Pair<List<Char>, List<Int>>, Long> = mutableMapOf()

    fun calculateMatches(): Long =
        calculateMatchesRecursive(
            chars = values,
            groups = groupSizes,
        )

    private fun calculateMatchesRecursive(
        chars: List<Char>,
        groups: List<Int>,
    ): Long {
        val key = chars to groups

        val cachedValue = cache[key]
        if (cachedValue != null) return cachedValue

        if (chars.isEmpty()) {
            // no chars are left. If a group is left then this result is invalid
            return if (groups.isEmpty()) 1 else 0
        }

        if (groups.isEmpty()) {
            // all groups are handled. If a required '#' char is still present then this result is invalid
            return if (chars.contains('#')) 0 else 1
        }

        var result = 0L
        val currentChar = chars.first()

        // if current char is a '.' or '?' then this char can be treated as not covered by a group -> recurse
        if (currentChar in listOf('.', '?')) {
            result += calculateMatchesRecursive(chars.drop(1), groups)
        }

        // if current char is a '#' or '?' then we check if the next group fits into the next chars
        if (currentChar in listOf('#', '?')) {
            val groupSize = groups.first()
            if (groupSize <= chars.size // current group fits into remaining characters
                && '.' !in chars.take(groupSize) // the next characters (covered by the group) do not contain a '.'
                && (groupSize == chars.size || chars[groupSize] != '#')  // the element after the group must not be covered by a group (it is not a '#')
            ) {
                // drop the next chars (including the reserved character after the group) and drop the currently matches group
                result += calculateMatchesRecursive(chars.drop(groupSize + 1), groups.drop(1))
            }
        }

        cache[key] = result

        return result
    }
}
