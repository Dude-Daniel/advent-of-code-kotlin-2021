package de.devdudes.aoc.core

abstract class Year(val resourceFolder: String) {

    abstract val days: List<Day>
    fun solveLastImplementedDay() {
        days.last { !it.ignored }.solve(resourceFolder)

        printIgnoredDays(days)
    }

    fun solveAllDays() {
        days.forEach { day -> day.solve(resourceFolder) }

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