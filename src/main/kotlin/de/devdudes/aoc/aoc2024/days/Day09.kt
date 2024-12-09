package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import java.math.BigInteger

class Day09 : Day(
    description = 9 - "Disk Fragmenter",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Fragment by moving File Blocks",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = BigInteger("1928"),
            solutionResult = BigInteger("6216544403458"),
            solution = { input ->
                parseDisk(input.first()).fragmentDiskByMovingSingleBlocks()
            }
        )

        puzzle(
            description = 2 - "Fragment by moving whole Files",
            input = "day09",
            testInput = "day09_test",
            expectedTestResult = BigInteger("2858"),
            solutionResult = BigInteger("6237075041489"),
            solution = { input ->
                parseDisk(input.first()).fragmentDiskByMovingFullFiles()
            }
        )
    }
)

private fun parseDisk(input: String): Disk =
    input.mapIndexed { index, size ->
        if (index % 2 == 0) {
            DiskFragment.Data(id = index / 2, size = size.digitToInt())
        } else {
            DiskFragment.Space(size = size.digitToInt())
        }
    }.let(::Disk)

private sealed class DiskFragment {
    abstract val size: Int

    data class Data(val id: Int, override val size: Int) : DiskFragment()
    data class Space(override val size: Int) : DiskFragment()
}

private class Disk(private val fragments: List<DiskFragment>) {

    private fun List<DiskFragment>.buildChecksum(): BigInteger {
        var index = 0
        var checksum = BigInteger.ZERO
        forEach { fragment ->
            when (fragment) {
                is DiskFragment.Data -> {
                    repeat(fragment.size) { subIndex ->
                        checksum += fragment.id.toBigInteger() * index.toBigInteger()
                        index++
                    }
                }

                is DiskFragment.Space -> {
                    index += fragment.size
                }
            }
        }
        return checksum
    }

    fun fragmentDiskByMovingSingleBlocks(): BigInteger {
        val flattenedDisk = fragments.flatMap { fragment ->
            when (fragment) {
                is DiskFragment.Data -> List(fragment.size) { fragment.copy(size = 1) }
                is DiskFragment.Space -> List(fragment.size) { fragment.copy(size = 1) }
            }
        }

        var startIndex = 0
        var endIndex = flattenedDisk.lastIndex
        val fragmentedDisk = flattenedDisk.toMutableList()

        while (startIndex < endIndex) {
            val startFragment = fragmentedDisk[startIndex]
            val endFragment = fragmentedDisk[endIndex]

            when (startFragment) {
                is DiskFragment.Data -> {
                    startIndex++
                }

                is DiskFragment.Space -> {
                    when (endFragment) {
                        is DiskFragment.Data -> {
                            fragmentedDisk[startIndex] = endFragment
                            fragmentedDisk[endIndex] = DiskFragment.Space(size = 1)
                            startIndex++
                        }

                        is DiskFragment.Space -> Unit
                    }
                    endIndex--
                }
            }
        }

        return fragmentedDisk.buildChecksum()
    }

    fun fragmentDiskByMovingFullFiles(): BigInteger {
        val dataBlocksToMove = fragments.filterIsInstance<DiskFragment.Data>().reversed()

        val fragmentedDisk = fragments.toMutableList()
        dataBlocksToMove.forEach { data ->
            val dataSize = data.size
            val dataIndex = fragmentedDisk.indexOfFirst { it == data }
            val newLocationIndex = fragmentedDisk.indexOfFirst { fragment -> fragment is DiskFragment.Space && fragment.size >= dataSize }

            if (newLocationIndex in 1..<dataIndex) {
                val space = fragmentedDisk[newLocationIndex] as DiskFragment.Space

                // swap the data
                fragmentedDisk[dataIndex] = DiskFragment.Space(size = dataSize)
                fragmentedDisk[newLocationIndex] = data

                // add spaces after the moves data so the total size stays the same
                if (space.size != dataSize) {
                    fragmentedDisk.add(newLocationIndex + 1, DiskFragment.Space(size = space.size - dataSize))
                }
            }
        }

        return fragmentedDisk.buildChecksum()
    }
}
