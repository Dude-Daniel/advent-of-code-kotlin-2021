package de.devdudes.aoc.core

import de.devdudes.aoc.core.Utils.formatDuration
import kotlin.system.measureTimeMillis

abstract class Year(val resourceFolder: String) {

    abstract val days: List<Day>
    fun solveLastImplementedDay() {
        days.lastOrNull { !it.ignored }?.solve(resourceFolder)

        printIgnoredDays(days)
    }

    fun solveAllDays() {
        val totalDuration = measureTimeMillis {
            days.forEach { day -> day.solve(resourceFolder) }
        }

        println("All Days of $resourceFolder solved in: ${formatDuration(totalDuration)}")

        printIgnoredDays(days)
    }

    private fun printIgnoredDays(days: List<Day>) {
        val ignoredDays = days.filter { it.ignored }.map { it.javaClass.simpleName }
        if (ignoredDays.isNotEmpty()) {
            println()
            println("Ignored days (not implemented yet): $ignoredDays")
        }
    }
}