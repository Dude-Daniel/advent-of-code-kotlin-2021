package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.aoc2023.days.InitializationStep.Operation
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day15 : Day(
    description = 15 - "Lens Library",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Holiday ASCII String Helper algorithm",
            input = "day15",
            testInput = "day15_test",
            expectedTestResult = 1320,
            solutionResult = 507_666,
            solution = { input ->
                parseInitializationSequence(input.first())
                    .calculateHash()
            }
        )

        puzzle(
            description = 2 - "Holiday ASCII String Helper Manual Arrangement Procedure",
            input = "day15",
            testInput = "day15_test",
            expectedTestResult = 145,
            solutionResult = 233_537,
            solution = { input ->
                parseInitializationSequence(input.first())
                    .calculateFocusingPower()
            }
        )
    }
)

private fun parseInitializationSequence(input: String): InitializationSequence =
    input.split(",")
        .map(::InitializationStep)
        .let(::InitializationSequence)

private data class InitializationSequence(val steps: List<InitializationStep>) {

    fun calculateHash(): Int = steps.sumOf { it.calculateHash() }

    private data class Lense(val label: String, val focalLength: Int)

    fun calculateFocusingPower(): Int {
        val boxes = putInBoxes()

        return boxes.map { (boxNumber, lenses) ->
            val boxValue = boxNumber + 1
            lenses.mapIndexed { index, lens ->
                boxValue * (index + 1) * lens.focalLength
            }
        }.flatten().sum()
    }

    private fun putInBoxes(): Map<Int, MutableList<Lense>> {
        val boxes: Map<Int, MutableList<Lense>> = buildMap {
            for (i in 0..255) put(i, mutableListOf())
        }

        steps.forEach { step ->
            val label = step.label
            val hash = label.holidayHash()

            when (step.operation) {
                Operation.ADD -> {
                    val boxContent = boxes[hash]!!
                    val existingIndex = boxContent.indexOfFirst { it.label == label }
                    if (existingIndex >= 0) {
                        boxContent[existingIndex] = Lense(label, step.focalLength)
                    } else {
                        boxContent.add(Lense(label, step.focalLength))
                    }
                }

                Operation.REMOVE -> {
                    boxes[hash]!!.removeAll { it.label == label }
                }
            }
        }

        return boxes
    }
}

private data class InitializationStep(val step: String) {

    enum class Operation { ADD, REMOVE }

    val operation: Operation by lazy { if (step.contains("-")) Operation.REMOVE else Operation.ADD }

    val label: String by lazy { step.split("-", "=").first() }

    val focalLength: Int by lazy { step.split("=").lastOrNull()?.toInt() ?: -1 }

    fun calculateHash(): Int = step.holidayHash()
}

private fun String.holidayHash(): Int =
    toCharArray()
        .fold(0) { acc: Int, char: Char ->
            ((acc + char.code) * 17) % 256
        }
