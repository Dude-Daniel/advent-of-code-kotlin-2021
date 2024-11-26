package de.devdudes.aoc.aoc2016.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day04 : Day(
    description = 4 - "Security Through Obscurity",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Sum of Sector Ids of real Rooms",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 2062,
            solutionResult = 173_787,
            solution = { input ->
                parseRooms(input)
                    .filter { it.isValid() }
                    .sumOf { it.sectorId }
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day04",
            testInput = "day04_test",
            expectedTestResult = 548,
            solutionResult = 548,
            solution = { input ->
                parseRooms(input)
                    .filter { it.isValid() }
                    .first { room ->
                        println("${room.sectorId} - ${room.decode()} - ${room.name}")
                        room.decode() == "northpole object storage"
                    }
                    .sectorId
            }
        )
    }
)

private fun parseRooms(input: List<String>): List<Room> =
    input.map { room ->
        val (nameWithId, checksum) = room.dropLast(1).split("[")
        val id = nameWithId.split("-").last()
        val name = nameWithId.removeSuffix("-$id")

        Room(
            name = name,
            sectorId = id.toInt(),
            checksum = checksum,
        )
    }

private data class Room(
    val name: String,
    val sectorId: Int,
    val checksum: String,
) {
    fun isValid(): Boolean {
        val digits = name.replace("-", "")
            .toCharArray()

        val lettersByCount = digits.distinct()
            .groupBy { char -> digits.count { it == char } }
            .toList()
            .sortedByDescending { it.first }

        var remainingLettersByCount = lettersByCount

        checksum.toCharArray()
            .forEach { char ->
                val letters = remainingLettersByCount.first()
                if (letters.second.contains(char)) {
                    remainingLettersByCount =
                        if (letters.second.size == 1) {
                            remainingLettersByCount.drop(1)
                        } else {
                            val remainingLetters = letters.second - char
                            buildList {
                                add(letters.copy(second = remainingLetters))
                                addAll(remainingLettersByCount.drop(1))
                            }
                        }
                } else {
                    return false
                }
            }

        return true
    }

    fun decode(): String {
        val alphabet = ('a'..'z').toList()

        return name.map { char ->
            if (char == '-') {
                ' '
            } else {
                val index = alphabet.indexOf(char) + sectorId
                val indexInAlphabet = index % alphabet.size
                alphabet[indexInAlphabet]
            }
        }.joinToString("")
    }
}
