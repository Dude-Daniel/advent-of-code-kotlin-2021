package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen
import de.devdudes.aoc.helpers.transpose

class Day05 : Day(
    description = 5 - "Supply Stacks",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "move one crate at a time",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = "CMZ",
            solutionResult = "QNNTGTPFN",
            solution = { input ->
                val commands = createSupplyStackCommands(input)
                val supplyStacks = createSupplyStacks(input)

                supplyStacks.reorder(
                    commands = commands,
                    operation = SingleItemStackMoveOperation(),
                ).stacks
                    .mapNotNull { it.value.lastOrNull() }
                    .joinToString(separator = "")
            }
        )

        puzzle(
            description = 2 - "move all crates at once",
            input = "day05",
            testInput = "day05_test",
            expectedTestResult = "MCD",
            solutionResult = "GGNPJBTTR",
            solution = { input ->
                val commands = createSupplyStackCommands(input)
                val supplyStacks = createSupplyStacks(input)

                supplyStacks.reorder(
                    commands = commands,
                    operation = BatchedStackMoveOperation(),
                ).stacks
                    .mapNotNull { it.value.lastOrNull() }
                    .joinToString(separator = "")
            }
        )
    }
)

private fun createSupplyStackCommands(input: List<String>): List<SupplyStackCommand> {
    val (_, commandsInput) = input.splitWhen { it.isEmpty() }

    return commandsInput.map { command ->
        val (quantity, _, from, _, to) =
            command.substring(startIndex = 5) // remove "move "
                .split(" ")

        SupplyStackCommand(
            from = from.toInt(),
            to = to.toInt(),
            quantity = quantity.toInt(),
        )
    }
}

private fun createSupplyStacks(input: List<String>): SupplyStacks {
    val (stackInput, _) = input.splitWhen { it.isEmpty() }

    val stacks = stackInput.map { line ->
        line.chunked(4)
            .mapIndexed { index, crateInput ->
                val crate = crateInput.trim()
                val crateName = when {
                    crate.isEmpty() -> null
                    else -> {
                        crate.substring(startIndex = 1).dropLast(1)
                    }
                }
                (index + 1) to crateName
            }
    }.dropLast(1)
        .transpose()
        .map { it.reversed() }
        .associate { stack ->
            stack.first().first to stack.mapNotNull { it.second }
        }

    return SupplyStacks(stacks = stacks)
}

private data class SupplyStacks(val stacks: Map<Int, List<String>>) {

    fun reorder(commands: List<SupplyStackCommand>, operation: StackMoveOperation): SupplyStacks {
        val mutableStacks = stacks.mapValues { (_, values) -> values.toMutableList() }

        commands.forEach { command ->
            operation.move(
                mutableStacks = mutableStacks,
                command = command
            )
        }

        return SupplyStacks(mutableStacks)
    }
}

private data class SupplyStackCommand(
    val from: Int,
    val to: Int,
    val quantity: Int,
)

private interface StackMoveOperation {
    fun move(mutableStacks: Map<Int, MutableList<String>>, command: SupplyStackCommand)
}

private class SingleItemStackMoveOperation : StackMoveOperation {
    override fun move(mutableStacks: Map<Int, MutableList<String>>, command: SupplyStackCommand) {
        repeat(command.quantity) {
            mutableStacks[command.to]!!.add(mutableStacks[command.from]!!.removeLast())
        }
    }
}

private class BatchedStackMoveOperation : StackMoveOperation {
    override fun move(mutableStacks: Map<Int, MutableList<String>>, command: SupplyStackCommand) {
        val cratesToMove = mutableStacks[command.from]!!.takeLast(command.quantity)
        repeat(command.quantity) { mutableStacks[command.from]!!.removeLast() }
        mutableStacks[command.to]!!.addAll(cratesToMove)
    }
}
