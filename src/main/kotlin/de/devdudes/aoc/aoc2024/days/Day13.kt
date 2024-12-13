package de.devdudes.aoc.aoc2024.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus
import de.devdudes.aoc.helpers.isWholeNumber
import de.devdudes.aoc.helpers.splitWhen
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class Day13 : Day(
    description = 13 - "Claw Contraption - Solve Equations",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "With Small Numbers",
            input = "day13",
            testInput = "day13_test",
            expectedTestResult = 480.toBigInteger(),
            solutionResult = 31_552.toBigInteger(),
            solution = { input ->
                parseClawMachines(input = input, prize = { it })
                    .calculatePrizeToWinAllPossiblePrizes()
            }
        )

        puzzle(
            description = 2 - "With Huge Numbers",
            input = "day13",
            testInput = "day13_test",
            expectedTestResult = 875318608908.toBigInteger(),
            solutionResult = 95273925552482.toBigInteger(),
            solution = { input ->
                parseClawMachines(input = input, prize = { it + 10_000_000_000_000L })
                    .calculatePrizeToWinAllPossiblePrizes()
            }
        )
    }
)

private fun parseClawMachines(input: List<String>, prize: (Long) -> Long): ClawMachineSolver {

    fun String.parseButton(): List<Long> = dropWhile { it != ':' }.drop(2).split(", ").map { it.drop(2).toLong() }

    return input.splitWhen { it.isBlank() }
        .map { (buttonAInput, buttonBInput, prizeInput) ->
            val (buttonAMovementX, buttonAMovementY) = buttonAInput.parseButton()
            val (buttonBMovementX, buttonBMovementY) = buttonBInput.parseButton()
            val (prizeLocationX, prizeLocationY) = prizeInput.parseButton()
            ClawMachine(
                buttonAMovementX to buttonAMovementY,
                buttonBMovementX to buttonBMovementY,
                prize(prizeLocationX) to prize(prizeLocationY),
            )
        }.let(::ClawMachineSolver)
}

private data class ClawMachine(
    val buttonAMovement: Pair<Long, Long>,
    val buttonBMovement: Pair<Long, Long>,
    val prizeLocation: Pair<Long, Long>,
)

private class ClawMachineSolver(private val clawMachines: List<ClawMachine>) {

    fun calculatePrizeToWinAllPossiblePrizes(): BigInteger =
        clawMachines.sumOf { clawMachine ->
            /*
             Given the requirement that each Prize Location needs to be reached by pressing buttons A and B
             results in the following equation:

             variables:
             pX -> prize location X
             pY -> prize location Y
             aX -> moves in direction X when pressing button A
             bX -> moves in direction X when pressing button B
             aY -> moves in direction Y when pressing button A
             bY -> moves in direction Y when pressing button B
             ca -> number of presses of button A
             cb -> number of presses of button B

             equations to solve:
             eq1: pX = aX * ca + bX * cb
             eq2: pY = aY * ca + bY * cb

             merge both equations into one by solving both equations to ca:
             eq1:
             aY * ca + bY * cb = pY      |  -(bY * cb)
             aY * ca = pY - (bY * cb)    |  :aY
             ca = (pY - (bY * cb)) : aY

             eq2:
             aX * ca + bX * cb = pX      |  -(bX * cb)
             aX * ca = pX - (bX * cb)    |  :aX
             ca = (pX - (bX * cb)) : aX

             eqComposed:
             (pY - (bY * cb)) : aY = (pX - (bX * cb)) : aX

             solve for cb:
             (pY - (bY * cb)) : aY = (pX - (bX * cb)) : aX             | *aY, *aX
             (pY - (bY * cb)) * aX = (pX - (bX * cb)) * aY             | remove ()
             (aX * pY) - (aX * bY * cb) = (aY * pX) - (aY * bX * cb)   | +(-(aX * pY) + (aY * bX * cb))
             (aX * pY) - (aX * bY * cb) + (-(aX * pY) + (aY * bX * cb)) = (aY * pX) - (aY * bX * cb) + (-(aX * pY) + (aY * bX * cb))
             (aX * pY) - (aX * bY * cb) -(aX * pY) + (aY * bX * cb) = (aY * pX) - (aY * bX * cb) -(aX * pY) + (aY * bX * cb)
             simplify by eliminating terms (aX * pY) and (aY * bX * cb):
             - (aX * bY * cb) + (aY * bX * cb) = (aY * pX) -(aX * pY)  | move cb out of parenthesis
             cb * (- (aX * bY) + (aY * bX)) = (aY * pX) -(aX * pY)     | :(- (aX * bY) + (aY * bX))
             cb = ((aY * pX) -(aX * pY)) : ((- (aX * bY) + (aY * bX))) | simplify
             cb = (aY * pX - aX * pY) : (- aX * bY + aY * bX)

             steps to solve equations:
             solve:                     cb = (aY * pX - aX * pY) : (- aX * bY + aY * bX)
             solve with result of cb:   ca = (pY - (bY * cb)) : aY
             if cb and ca are whole number -> a valid solution was found
             else -> there is no solution
             */

            val aX = clawMachine.buttonAMovement.first.toBigDecimal()
            val aY = clawMachine.buttonAMovement.second.toBigDecimal()
            val bX = clawMachine.buttonBMovement.first.toBigDecimal()
            val bY = clawMachine.buttonBMovement.second.toBigDecimal()
            val pX = clawMachine.prizeLocation.first.toBigDecimal()
            val pY = clawMachine.prizeLocation.second.toBigDecimal()

            // solve: cb = (aY * pX - aX * pY) / (- aX * bY + aY * bX)
            val cbLeft = aY * pX - aX * pY
            val cbRight = -aX * bY + aY * bX
            val cb = cbLeft.divide(cbRight, 2, RoundingMode.HALF_EVEN)

            // solve: ca = (pY - (bY * cb)) / aY
            val caLeft = pY - (bY * cb)
            val ca = caLeft.divide(aY, 2, RoundingMode.HALF_EVEN)

            if (ca.isWholeNumber() && cb.isWholeNumber()) {
                cost(buttonAMoves = ca, buttonBMoves = cb)
            } else {
                // the priceLocation cannot be reached
                BigInteger.ZERO
            }
        }

    private fun cost(buttonAMoves: BigDecimal, buttonBMoves: BigDecimal): BigInteger = ((buttonAMoves * 3.toBigDecimal()) + buttonBMoves).toBigInteger()
}
