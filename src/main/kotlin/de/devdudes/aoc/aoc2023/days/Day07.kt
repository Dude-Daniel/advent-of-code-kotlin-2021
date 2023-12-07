package de.devdudes.aoc.aoc2023.days

import de.devdudes.aoc.core.Day
import de.devdudes.aoc.core.minus

class Day07 : Day(
    description = 7 - "Camel Cards - Total Winnings",
    ignored = false,
    days = {
        puzzle(
            description = 1 - "Without Jokers",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 6440,
            solutionResult = 250_254_244,
            solution = { input ->
                parseCamelCardGame(input = input, jokersAllowed = false)
                    .sortHandsByTheirRank()
                    .reversed()
                    .mapIndexed { index, camelCardHand -> (index + 1) * camelCardHand.bid }
                    .sum()
            }
        )

        puzzle(
            description = 2 - "With Jokers",
            input = "day07",
            testInput = "day07_test",
            expectedTestResult = 5905,
            solutionResult = 250_087_440,
            solution = { input ->
                parseCamelCardGame(input = input, jokersAllowed = true)
                    .sortHandsByTheirRank()
                    .reversed()
                    .mapIndexed { index, camelCardHand -> (index + 1) * camelCardHand.bid }
                    .sum()
            }
        )
    }
)

private fun parseCamelCardGame(input: List<String>, jokersAllowed: Boolean): CamelCardGame =
    input.map { line ->
        val (cards, bid) = line.split(" ")

        CamelCardHand(
            cards = cards.toCharArray().toList(),
            bid = bid.toInt(),
            jokersAllowed = jokersAllowed,
        )
    }.let(::CamelCardGame)

private data class CamelCardGame(
    val hands: List<CamelCardHand>,
) {

    fun sortHandsByTheirRank(): List<CamelCardHand> {
        val ranks = ranks(hands.first().jokersAllowed)

        return hands.sortedWith { first, second ->
            val handTypeComparison = first.handType.compareTo(second.handType)
            if (handTypeComparison != 0) {
                // hand types differ so sort by it
                handTypeComparison
            } else {
                // hand types are equal so sort by comparing cards
                first.cards.zip(second.cards)
                    .firstNotNullOf { (firstCard, secondCard) ->
                        val cardComparison = ranks.indexOf(firstCard).compareTo(ranks.indexOf(secondCard))
                        // if cards match compare the next cards (return null)
                        if (cardComparison == 0) null else cardComparison
                    }
            }
        }
    }

    private companion object {

        private fun ranks(jokersAllowed: Boolean): List<Char> =
            if (jokersAllowed) listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
            else listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')

    }
}

private data class CamelCardHand(
    val cards: List<Char>,
    val bid: Int,
    val jokersAllowed: Boolean,
) {

    private data class CardWithCount(val card: Char, val count: Int)

    val handType by lazy {
        val jokerCount =
            if (jokersAllowed) cards.mapNotNull { if (it == 'J') 1 else null }.sum()
            else 0

        val cardsWithCount = cards.groupBy { it }
            .mapNotNull { (card, cards) -> if (jokersAllowed && card == 'J') null else CardWithCount(card, cards.size) }
            // if jokers are allowed then filter out any cards which are jokers, as these are added manually later on
            .filter { !(jokersAllowed && it.card == 'J') }
            .sortedByDescending { it.count }

        when {
            cardsWithCount.isEmpty() -> CamelCardHandType.FIVE_OF_A_KIND
            cardsWithCount.first().count + jokerCount == 5 -> CamelCardHandType.FIVE_OF_A_KIND
            cardsWithCount.first().count + jokerCount == 4 -> CamelCardHandType.FOUR_OF_A_KIND
            cardsWithCount.first().count + jokerCount == 3 && cardsWithCount[1].count == 2 -> CamelCardHandType.FULL_HOUSE
            cardsWithCount.first().count + jokerCount == 3 -> CamelCardHandType.THREE_OF_A_KIND
            cardsWithCount.first().count + jokerCount == 2 && cardsWithCount[1].count == 2 -> CamelCardHandType.TWO_PAIR
            cardsWithCount.first().count + jokerCount == 2 -> CamelCardHandType.ONE_PAIR
            else -> CamelCardHandType.HIGH_CARD
        }
    }

}

private enum class CamelCardHandType {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD,
}
