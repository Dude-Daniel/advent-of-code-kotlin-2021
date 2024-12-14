package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.Grid2D
import de.devdudes.aoc.helpers.Point
import de.devdudes.aoc.helpers.plus
import de.devdudes.aoc.helpers.print
import de.devdudes.aoc.helpers.times

class Day14 : Day(
    description = 14 - "Restroom Redoubt",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Positions after 100 Seconds",
            input = "day14",
            testInput = "day14_test",
            expectedTestResult = 12,
            solutionResult = 228_410_028,
            solution = { input ->
                if (isTest) {
                    parseRobots(input).calculateSafetyFactor(steps = 100, maxWidth = 11, maxHeight = 7)
                } else {
                    parseRobots(input).calculateSafetyFactor(steps = 100, maxWidth = 101, maxHeight = 103)
                }
            }
        )

        puzzle(
            description = 2 - "Seconds to find the Easter Egg (the Christmas Tree)",
            input = "day14",
            testInput = "day14_test",
            expectedTestResult = Unit,
            solutionResult = 8_258,
            solution = { input ->
                if (isTest) {
                    Unit // there is no easter egg on the test data
                } else {
                    parseRobots(input).findChristmasTree(maxWidth = 101, maxHeight = 103)
                }
            }
        )
    }
)

private fun parseRobots(input: List<String>): RobotSolver =
    input.map { line ->
        val (rawPosition, rawVelocity) = line.split(" ")
        val (startX, startY) = rawPosition.drop(2).split(",").map { it.toInt() }
        val (velocityX, velocityY) = rawVelocity.drop(2).split(",").map { it.toInt() }
        Robot(
            position = Point(startX, startY),
            velocity = Point(velocityX, velocityY),
        )
    }.let(::RobotSolver)

private data class Robot(
    val position: Point,
    val velocity: Point,
) {
    fun move(steps: Int, maxWidth: Int, maxHeight: Int): Robot {
        val totalDistance = velocity * steps
        val newPosition = position + totalDistance
        val newPositionX = Math.floorMod(newPosition.x, maxWidth)
        val newPositionY = Math.floorMod(newPosition.y, maxHeight)
        return copy(position = Point(newPositionX, newPositionY))
    }
}

private class RobotSolver(private val robots: List<Robot>) {

    fun calculateSafetyFactor(steps: Int, maxWidth: Int, maxHeight: Int): Int {
        val movedRobots = robots.map { robot ->
            robot.move(steps = steps, maxWidth = maxWidth, maxHeight = maxHeight)
        }

        movedRobots.printRobots(maxWidth, maxHeight)

        val center = Point(maxWidth / 2, maxHeight / 2)
        return movedRobots.groupBy { robot ->
            when {
                robot.position.x < center.x && robot.position.y < center.y -> 0
                robot.position.x < center.x && robot.position.y > center.y -> 1
                robot.position.x > center.x && robot.position.y < center.y -> 2
                robot.position.x > center.x && robot.position.y > center.y -> 3
                else -> -1
            }
        }.entries
            .filter { it.key >= 0 }
            .fold(1) { acc, groupedRobots -> acc * groupedRobots.value.size }
    }

    fun findChristmasTree(maxWidth: Int, maxHeight: Int): Int {
        var movingRobots = robots

        var currentStep = 0

        // iterate until we found the christmas tree
        while (true) {
            currentStep += 1
            movingRobots = movingRobots.map { robot ->
                robot.move(steps = 1, maxWidth = maxWidth, maxHeight = maxHeight)
            }

            val minCountOfNeighborRobots = 30 // the count of robots that must be places in a straight line

            val matches = movingRobots
                .asSequence()
                .map { it.position }
                .distinct()
                .sortedBy { it.x } // make sure the points are ascending so the calculation down below works
                .groupBy { it.y } // group by rows
                .filterValues { it.size >= minCountOfNeighborRobots } // already skip all rows which are not long enough
                .firstNotNullOfOrNull { (_, points) ->
                    points
                        .windowed(minCountOfNeighborRobots, 1) // look at all points in batches of minCountOfNeighborRobots
                        .firstOrNull { subList ->
                            // when the first and last point are exactly minCountOfNeighborRobots apart then all points are next to each other
                            subList.first().x + minCountOfNeighborRobots - 1 == subList.last().x
                        }
                }

            if (matches != null) {
                // Tree was found
                movingRobots.printRobots(maxWidth, maxHeight)
                return currentStep
            }

            if (robots == movingRobots) {
                // we are at the start -> the tree was not found
                println("Start Reached: $currentStep")
                return -1
            }
        }
    }

    private fun List<Robot>.printRobots(maxWidth: Int, maxHeight: Int) {
        val robotsAtPosition = this.groupBy { it.position }

        Grid2D(maxWidth, maxHeight) { point ->
            when (val robotCount = robotsAtPosition.getOrElse(point) { emptyList() }.size) {
                0 -> "."
                else -> "$robotCount"
            }
        }.print { it }
    }
}
