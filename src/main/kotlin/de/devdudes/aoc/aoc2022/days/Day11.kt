package de.devdudes.aoc.aoc2022.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen
import java.math.BigInteger

class Day11 : Day(
    description = 11 - "Monkey in the Middle",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Monkey Business after 20 rounds",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 10605.toBigInteger(),
            solutionResult = 50616.toBigInteger(),
            solution = { input ->
                val monkeys = parseMonkeys(input)
                val monkeyThrowPrediction = MonkeyThrowPrediction(
                    monkeys = monkeys,
                    divideWorryByThree = true,
                )
                monkeyThrowPrediction.simulateRounds(20)
                monkeyThrowPrediction.calculateMonkeyBusiness()
            }
        )

        puzzle(
            description = 2 - "Monkey Business after 10.000 rounds",
            input = "day11",
            testInput = "day11_test",
            expectedTestResult = 2713310158.toBigInteger(),
            solutionResult = 11309046332.toBigInteger(),
            solution = { input ->
                val monkeys = parseMonkeys(input)
                val monkeyThrowPrediction = MonkeyThrowPrediction(
                    monkeys = monkeys,
                    divideWorryByThree = false,
                )
                monkeyThrowPrediction.simulateRounds(10_000)
                monkeyThrowPrediction.calculateMonkeyBusiness()
            }
        )
    }
)

private fun parseMonkeys(input: List<String>): List<Monkey> {
    return input.splitWhen { it.isEmpty() }
        .map { monkeyInput ->
            val values = monkeyInput[1]
                .split(": ").last()
                .split(", ").map { it.toBigInteger() }

            val operation = monkeyInput[2]
                .split("new = old ").last()
                .split(" ")
                .let { (operation, amount) ->
                    when {
                        amount == "old" -> MonkeyOperation.Square
                        operation == "*" -> MonkeyOperation.Multiply(amount.toBigInteger())
                        else -> MonkeyOperation.Add(amount.toBigInteger())
                    }
                }

            Monkey(
                id = monkeyInput[0].split(" ").last().dropLast(1).toInt(),
                itemValues = values,
                operation = operation,
                testDivisible = monkeyInput[3].split(" ").last().toBigInteger(),
                testTrueMonkeyId = monkeyInput[4].split(" ").last().toInt(),
                testFalseMonkeyId = monkeyInput[5].split(" ").last().toInt(),
            )
        }
}

private data class Monkey(
    val id: Int,
    var itemValues: List<BigInteger>,
    val operation: MonkeyOperation,
    val testDivisible: BigInteger,
    val testTrueMonkeyId: Int,
    val testFalseMonkeyId: Int,
) {
    var inspectionCount: BigInteger = BigInteger.ZERO
        private set

    fun increaseInspectionCount() {
        inspectionCount += BigInteger.ONE
    }
}

private sealed class MonkeyOperation {

    abstract fun calculate(input: BigInteger): BigInteger

    data class Multiply(val value: BigInteger) : MonkeyOperation() {
        override fun calculate(input: BigInteger): BigInteger = value * input
    }

    data class Add(val value: BigInteger) : MonkeyOperation() {
        override fun calculate(input: BigInteger): BigInteger = value + input
    }

    object Square : MonkeyOperation() {
        override fun calculate(input: BigInteger): BigInteger = input * input
    }
}

private class MonkeyThrowPrediction(
    private val monkeys: List<Monkey>,
    private val divideWorryByThree: Boolean,
) {

    private val mutableMonkeys = monkeys.toMutableList()
    private val combinedDivisor = monkeys.map { it.testDivisible }
        .fold(BigInteger.ONE) { acc, value -> acc * value }

    fun simulateRounds(rounds: Int) =
        repeat(rounds) {
            simulateRound()
        }

    private fun simulateRound() {
        mutableMonkeys.forEach { monkey: Monkey ->
            monkey.itemValues.forEach { itemValue ->
                monkey.increaseInspectionCount()

                var newItemValue = monkey.operation.calculate(itemValue)
                if (divideWorryByThree) newItemValue /= 3.toBigInteger()
                else newItemValue %= combinedDivisor

                val newMonkeyId =
                    if (newItemValue.mod(monkey.testDivisible) == BigInteger.ZERO) monkey.testTrueMonkeyId
                    else monkey.testFalseMonkeyId

                val newMonkey = mutableMonkeys.first { it.id == newMonkeyId }
                newMonkey.itemValues = newMonkey.itemValues + newItemValue
            }

            monkey.itemValues = emptyList()
        }
    }

    fun calculateMonkeyBusiness(): BigInteger {
        val (c1, c2) = monkeys.sortedByDescending { it.inspectionCount }
            .take(2)
            .map { it.inspectionCount }
        return c1 * c2
    }
}
