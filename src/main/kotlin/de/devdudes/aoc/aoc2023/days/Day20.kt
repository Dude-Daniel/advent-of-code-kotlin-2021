package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.aoc2023.days.PropagationModule.BroadcastModule
import de.devdudes.aoc.aoc2023.days.PropagationModule.ConjunctionModule
import de.devdudes.aoc.aoc2023.days.PropagationModule.FlipFlopModule
import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.lcm

class Day20 : Day(
    description = 20 - "Pulse Propagation",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "1000 Button Presses",
            input = "day20",
            testInput = "day20_test",
            expectedTestResult = 32_000_000,
            solutionResult = 825_167_435,
            solution = { input ->
                parsePulsePropagation(input)
                    .sendSignal(1000)
            }
        )

        puzzle(
            description = 2 - "Unknown",
            input = "day20",
            testInput = "day20_test_second",
            expectedTestResult = 1L,
            solutionResult = 225_514_321_828_633L,
            solution = { input ->
                parsePulsePropagation(input)
                    .numberOfButtonPressesToReachRx()
            }
        )
    }
)

private fun parsePulsePropagation(input: List<String>): PulsePropagation {
    val rawModules = input.map { line ->
        val (rawTypeAndName, rawTargets) = line.split(" -> ")
        rawTypeAndName to rawTargets.split(", ")
    }

    val allNames = rawModules.flatMap { entry ->
        entry.second.map { name -> name.replace("%", "").replace("&", "") }
    }.distinct()

    val inputs = allNames.map { name ->
        val inputs = rawModules.filter { (_, nestedTargets) ->
            nestedTargets.contains(name)
        }.map { it.first.replace("%", "").replace("&", "") }
        name to inputs
    }.associateBy(keySelector = { it.first }) { it.second }

    return rawModules.map { (rawTypeAndName, targets) ->

        when (rawTypeAndName.first()) {
            '%' -> FlipFlopModule(
                name = rawTypeAndName.drop(1),
                targets = targets,
            )

            '&' -> ConjunctionModule(
                name = rawTypeAndName.drop(1),
                targets = targets,
                inputs = inputs.getValue(rawTypeAndName.drop(1)),
            )

            else -> BroadcastModule(
                name = rawTypeAndName,
                targets = targets,
            )
        }
    }.associateBy { it.name }
        .let { PulsePropagation(modules = it, inputMappings = inputs) }
}

private data class PulsePropagation(
    val modules: Map<String, PropagationModule>,
    val inputMappings: Map<String, List<String>>,
) {

    fun numberOfButtonPressesToReachRx(): Long {
        val moduleNameToReachRx = inputMappings.getValue("rx").first()
        val moduleNamesToReachModuleBeforeRx = inputMappings.getValue(moduleNameToReachRx)

        val remainingSignals: MutableList<PulseSignal> = mutableListOf()

        val lcms = mutableMapOf<String, Long>()
        var counter = 0L

        while (lcms.size != moduleNamesToReachModuleBeforeRx.size) {

            // count button pushes and initialize button push with low signal on broadcaster

            counter += 1
            remainingSignals.add(PulseSignal.Low(source = "NONE", target = "broadcaster"))

            // handle button push
            while (remainingSignals.isNotEmpty()) {
                val signal = remainingSignals.removeFirst()

                // when signal is low and signal targets the module before rx then store this counter
                // (when all signals are low at the same time then rx will also be low)
                if (signal is PulseSignal.Low && moduleNamesToReachModuleBeforeRx.contains(signal.target)) {
                    if (!lcms.containsKey(signal.target)) lcms[signal.target] = counter
                }

                val module = modules[signal.target]
                if (module != null) {
                    val newSignals = module.process(signal)
                    remainingSignals.addAll(newSignals)
                }
            }
        }

        // calculate first low signal to rx by calculating the lcm of all stored counters
        return lcms.values.reduce { acc, value -> lcm(acc, value) }
    }

    fun sendSignal(times: Int): Int {
        var highCounter = 0
        var lowCounter = 0

        repeat(times) {
            val counts = pushButton()
            highCounter += counts.high
            lowCounter += counts.low
        }

        return lowCounter * highCounter
    }

    private data class Counters(val low: Int, val high: Int)

    private fun pushButton(): Counters {
        val remainingSignals: MutableList<PulseSignal> =
            mutableListOf(PulseSignal.Low(source = "NONE", target = "broadcaster"))

        var highCounter = 0
        var lowCounter = 1 // the first signal is also sent
        while (remainingSignals.isNotEmpty()) {
            val signal = remainingSignals.removeFirst()

            val module = modules[signal.target]
            if (module != null) {
                val newSignals = module.process(signal)
                remainingSignals.addAll(newSignals)

                newSignals.forEach { newSignal ->
                    if (newSignal is PulseSignal.Low) lowCounter += 1 else highCounter += 1
                }
            }

        }
        return Counters(low = lowCounter, high = highCounter)
    }
}

private sealed class PulseSignal {
    abstract val source: String
    abstract val target: String

    data class Low(
        override val source: String,
        override val target: String,
    ) : PulseSignal()

    data class High(
        override val source: String,
        override val target: String,
    ) : PulseSignal()
}

private sealed interface PropagationModule {

    val name: String
    val targets: List<String>

    fun process(signal: PulseSignal): List<PulseSignal>

    data class FlipFlopModule(
        override val name: String,
        override val targets: List<String>,
    ) : PropagationModule {

        private var isOn = false

        override fun process(signal: PulseSignal): List<PulseSignal> =
            when (signal) {
                is PulseSignal.Low -> {
                    if (isOn) {
                        isOn = false
                        targets.map { PulseSignal.Low(source = name, target = it) }
                    } else {
                        isOn = true
                        targets.map { PulseSignal.High(source = name, target = it) }
                    }
                }

                else -> emptyList()
            }
    }

    data class ConjunctionModule(
        override val name: String,
        override val targets: List<String>,
        val inputs: List<String>,
    ) : PropagationModule {

        val onStateMappings = inputs.associateWith { false }.toMutableMap()

        override fun process(signal: PulseSignal): List<PulseSignal> {
            onStateMappings[signal.source] = signal is PulseSignal.High

            return if (onStateMappings.all { it.value }) targets.map { PulseSignal.Low(source = name, target = it) }
            else targets.map { PulseSignal.High(source = name, target = it) }
        }
    }

    data class BroadcastModule(
        override val name: String,
        override val targets: List<String>,
    ) : PropagationModule {

        override fun process(signal: PulseSignal): List<PulseSignal> {
            return if (signal is PulseSignal.Low) targets.map { PulseSignal.Low(source = name, target = it) }
            else targets.map { PulseSignal.High(source = name, target = it) }
        }
    }
}
