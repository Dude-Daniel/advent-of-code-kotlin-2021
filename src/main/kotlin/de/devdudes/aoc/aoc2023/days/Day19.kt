package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.splitWhen
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

class Day19 : Day(
    description = 19 - "Aplenty - Filter Parts",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Find Matching Parts",
            input = "day19",
            testInput = "day19_test",
            expectedTestResult = 19_114,
            solutionResult = 449_531,
            solution = { input ->
                parsePartsWithWorkflows(input)
                    .calculateAcceptedParts()
                    .sumOf { it.sum() }
            }
        )

        puzzle(
            description = 2 - "Number of distinct combinations of ratings",
            input = "day19",
            testInput = "day19_test",
            expectedTestResult = BigInteger("167409079868000"),
            solutionResult = BigInteger("122756210763577"),
            solution = { input ->
                parsePartsWithWorkflows(input)
                    .calculateNumberOfDistinctRatings()
            }
        )
    }
)

private fun parsePartsWithWorkflows(input: List<String>): PartsWithWorkflows {
    val (rawWorkflows, rawParts) = input.splitWhen { it.isEmpty() }

    // parse workflows
    val workflows = rawWorkflows.map { workflow ->
        val name = workflow.takeWhile { it != '{' }

        val rules = workflow
            // drop '<name>{' and '}'
            .drop(name.length + 1).dropLast(1)
            .split(",")
            .map { parseWorkflowRule(it) }

        MachineWorkflow(
            name = name,
            rules = rules,
        )
    }

    // parse parts
    val parts = rawParts.map { part ->
        val (x, m, a, s) = part
            // drop '{' and '}'
            .drop(1).dropLast(1)
            .split(",").map { it.drop(2).toInt() }
        MachinePart(x = x, m = m, a = a, s = s)
    }

    return PartsWithWorkflows(workflows = workflows.associateBy { it.name }, parts = parts)
}

private fun parseWorkflowRule(input: String): WorkflowRule {

    fun parseTarget(name: String): WorkflowTarget =
        when (name) {
            "A" -> WorkflowTarget.Success
            "R" -> WorkflowTarget.Failure
            else -> WorkflowTarget.Workflow(name)
        }

    return when {
        input.contains(":") -> {
            val (condition, target) = input.split(":")
            WorkflowRule.ConditionRule(
                condition = condition,
                target = parseTarget(target)
            )
        }

        else -> WorkflowRule.TargetRule(parseTarget(input))
    }
}

private data class PartsWithWorkflows(
    val workflows: Map<String, MachineWorkflow>,
    val parts: List<MachinePart>,
) {

    private val startWorkflow: MachineWorkflow = workflows.getValue("in")

    fun calculateNumberOfDistinctRatings(): BigInteger =
        calculateDistinctRatings(range = MachinePartRange(), workflow = startWorkflow)

    private fun calculateDistinctRatings(range: MachinePartRange, workflow: MachineWorkflow): BigInteger {
        var currentRange = range

        return workflow.rules.sumOf { rule ->
            when (rule) {
                is WorkflowRule.ConditionRule -> {
                    when (rule.target) {
                        WorkflowTarget.Failure -> BigInteger.ZERO
                        WorkflowTarget.Success -> rule.applyRange(currentRange).distinctRatings()
                        is WorkflowTarget.Workflow ->
                            calculateDistinctRatings(
                                range = rule.applyRange(currentRange),
                                workflow = workflows.getValue(rule.target.name),
                            )
                    }.also {
                        currentRange = rule.subtractRange(currentRange)
                    }
                }

                is WorkflowRule.TargetRule ->
                    when (rule.target) {
                        WorkflowTarget.Failure -> BigInteger.ZERO
                        WorkflowTarget.Success -> currentRange.distinctRatings()
                        is WorkflowTarget.Workflow ->
                            calculateDistinctRatings(
                                range = currentRange,
                                workflow = workflows.getValue(rule.target.name)
                            )
                    }
            }
        }
    }

    fun calculateAcceptedParts(): List<MachinePart> =
        parts.filter { part ->
            findNextTarget(part, startWorkflow) == WorkflowTarget.Success
        }

    private fun findNextTarget(part: MachinePart, workflow: MachineWorkflow): WorkflowTarget {
        workflow.rules.forEach { rule ->
            val target = when (rule) {
                is WorkflowRule.ConditionRule -> if (rule.matches(part)) rule.target else null
                is WorkflowRule.TargetRule -> rule.target
            }

            if (target != null) {
                return when (target) {
                    WorkflowTarget.Failure,
                    WorkflowTarget.Success,
                    -> target

                    is WorkflowTarget.Workflow ->
                        findNextTarget(part, workflows.getValue(target.name))
                }
            }
        }

        throw Exception("a target should be found or something is wrong")
    }
}

private data class MachinePart(
    val x: Int,
    val m: Int,
    val a: Int,
    val s: Int,
) {
    fun sum(): Int = x + m + a + s
}

private data class MachinePartRange(
    val x: IntRange = 1..4000,
    val m: IntRange = 1..4000,
    val a: IntRange = 1..4000,
    val s: IntRange = 1..4000,
) {

    private val IntRange.size: BigInteger
        get() =
            if (endInclusive > start) endInclusive.toBigInteger() - start.toBigInteger() + BigInteger.ONE
            else BigInteger.ZERO

    fun distinctRatings(): BigInteger = x.size * m.size * a.size * s.size
}

private data class MachineWorkflow(
    val name: String,
    val rules: List<WorkflowRule>,
)

private sealed class WorkflowTarget {
    data object Success : WorkflowTarget()
    data object Failure : WorkflowTarget()
    data class Workflow(val name: String) : WorkflowTarget()
}

private sealed class WorkflowRule {

    data class TargetRule(val target: WorkflowTarget) : WorkflowRule()

    data class ConditionRule(val condition: String, val target: WorkflowTarget) : WorkflowRule() {
        private val conditionValue: Int =
            condition.drop(
                condition.indexOfAny(listOf("<", ">")) + 1
            ).toInt()

        private val isLessThanExpression: Boolean by lazy { condition.contains("<") }

        private val conditionExpression: (Int) -> Boolean by lazy {
            if (isLessThanExpression) {
                { value -> value < conditionValue }
            } else {
                { value -> value > conditionValue }
            }
        }

        fun matches(part: MachinePart): Boolean =
            when (condition.first()) {
                'x' -> conditionExpression(part.x)
                'm' -> conditionExpression(part.m)
                'a' -> conditionExpression(part.a)
                's' -> conditionExpression(part.s)
                else -> throw UnsupportedOperationException("unsupported argument: ${condition.first()}")
            }

        private fun MachinePartRange.updateValue(start: (Int) -> Int, end: (Int) -> Int): MachinePartRange =
            when (condition.first()) {
                'x' -> this.copy(x = start(this.x.first)..end(this.x.last))
                'm' -> this.copy(m = start(this.m.first)..end(this.m.last))
                'a' -> this.copy(a = start(this.a.first)..end(this.a.last))
                's' -> this.copy(s = start(this.s.first)..end(this.s.last))
                else -> throw UnsupportedOperationException("unsupported argument: ${condition.first()}")
            }

        fun applyRange(range: MachinePartRange): MachinePartRange =
            if (isLessThanExpression) {
                range.updateValue(
                    start = { it },
                    end = { min(it, conditionValue - 1) },
                )
            } else {
                range.updateValue(
                    start = { max(it, conditionValue + 1) },
                    end = { it },
                )
            }

        fun subtractRange(range: MachinePartRange): MachinePartRange =
            if (isLessThanExpression) {
                range.updateValue(
                    start = { max(it, conditionValue) },
                    end = { it },
                )
            } else {
                range.updateValue(
                    start = { it },
                    end = { min(it, conditionValue) },
                )
            }
    }
}
