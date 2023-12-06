package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitByRange
import de.devdudes.aoc.helpers.splitWhen

class Day05 : Day(
    description = 5 - "If You Give A Seed A Fertilizer - Lowest location number",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Single Seeds",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 35L,
            solutionResult = 177_942_185L,
            solution = { input ->
                val almanac = parsePlantAlmanac(input) { seedNumbers ->
                    // each number is a single seed (range)
                    seedNumbers.map { it..it }
                }

                almanac.seeds.flatMap { seed ->
                    almanac.mapSeedRange(seed)
                }.minOf { it.first }
            }
        )

        puzzle(
            description = 2 - "Seed Ranges",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = 46L,
            solutionResult = 69_841_803L,
            solution = { input ->
                val almanac = parsePlantAlmanac(input) { seedNumbers ->
                    // convert pairs of numbers into ranges (start, count)
                    seedNumbers.chunked(2) { (start, count) ->
                        start until (start + count)
                    }
                }

                almanac.seeds.flatMap { seed ->
                    almanac.mapSeedRange(seed)
                }.minOf { it.first }
            }
        )
    }
)

/**
 * Parse the following input to a [PlantAlmanac]
 *
 * ```
 * seeds: 79 14 55 13
 *
 * seed-to-soil map:
 * 50 98 2
 * 52 50 48
 *
 * soil-to-fertilizer map:
 * 0 15 37
 * 37 52 2
 * 39 0 15
 * ```
 */
private fun parsePlantAlmanac(
    input: List<String>,
    mapSeeds: (List<Long>) -> List<LongRange>,
): PlantAlmanac {
    // parse seeds: "seeds: 79 14 55 13"
    val seeds = input.first()
        .substringAfter(": ")
        .split(" ")
        .map { it.toLong() }
        .let(mapSeeds)

    // parse all maps
    val mappings = input.drop(2) // drop the first two lines as they are seeds
        .splitWhen { it.isEmpty() } // split on empty lines, so we have a list of maps
        .map { group ->
            group.drop(1) // get rid of the group name
                .map { mappingLine ->
                    // extract three numbers from every line (destination, source and length)
                    val (destination, source, length) = mappingLine.split(" ")
                    AlmanacPlantMapping(
                        source = source.toLong(),
                        destination = destination.toLong(),
                        length = length.toLong(),
                    )
                }.let(::AlmanacMappingGroup)
        }

    return PlantAlmanac(seeds = seeds, mappings = mappings)
}

private data class PlantAlmanac(
    val seeds: List<LongRange>,
    val mappings: List<AlmanacMappingGroup>,
) {

    fun mapSeedRange(seeds: LongRange): List<LongRange> =
        mappings.scan(listOf(seeds)) { sourceRanges, mappingGroup ->
            sourceRanges.flatMap {
                mappingGroup.map(it)
            }
        }.last()

}

private data class AlmanacPlantMapping(
    val source: Long,
    val destination: Long,
    val length: Long,
) {
    val offset = destination - source
    val sourceRange by lazy { LongRange(source, source + length - 1) }
}

private data class AlmanacMappingGroup(
    private val mappings: List<AlmanacPlantMapping>,
) {
    fun map(range: LongRange): List<LongRange> {
        var uncoveredRanges = listOf(range)
        val mappedRanges = mutableListOf<LongRange>()

        // Each range is mapped by this mapping group. One range may be mapped by different
        // mappings. This means that the initial range may be split up. If one mapping
        // divides the initial range into multiple ones then there will be at least one
        // matching range (accumulated in mappedRanges) and between zero and two ranges
        // which are not covered yet (stored in uncoveredRanges). These ranges are then
        // evaluated by the next mapping and may be split up even further.
        mappings.forEach { mapping ->
            val newUncoveredRanges = uncoveredRanges.flatMap { uncoveredRange ->
                val (foundMatchingRange, foundUncoveredRanges) = uncoveredRange.splitByRange(mapping.sourceRange)

                // add found range to mapped ranges (and change to destination range)
                if (foundMatchingRange != null) {
                    LongRange(
                        start = foundMatchingRange.first + mapping.offset,
                        endInclusive = foundMatchingRange.last + mapping.offset,
                    ).let(mappedRanges::add)
                }

                // return new uncovered ranges, so they are collected into a list
                foundUncoveredRanges
            }

            uncoveredRanges = newUncoveredRanges
        }

        return mappedRanges + uncoveredRanges
    }
}
