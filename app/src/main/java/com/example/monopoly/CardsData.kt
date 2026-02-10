package com.example.monopoly

import kotlin.random.Random

data class Card(
    val text: String,
    val effect: CardEffect
)

sealed class CardEffect {
    data class Money(val amount: Int) : CardEffect()
    data class MoveTo(val index: Int, val collectGo: Boolean = false) : CardEffect()
    data class MoveSteps(val steps: Int) : CardEffect()
    data class MoveToNearest(val type: SquareType) : CardEffect()
    data class PayEachPlayer(val amount: Int) : CardEffect()
    data class CollectFromEachPlayer(val amount: Int) : CardEffect()
    object GoToJail : CardEffect()
    object None : CardEffect()
}

class CardDeck(cards: List<Card>) {
    private val deck: List<Card> = cards.shuffled(Random(System.currentTimeMillis()))
    private var index = 0

    fun draw(): Card {
        if (deck.isEmpty()) {
            return Card("Brak kart", CardEffect.None)
        }
        val card = deck[index]
        index = (index + 1) % deck.size
        return card
    }
}

object CardsData {
    val chanceCards: List<Card> = listOf(
        Card("Advance to GO (Collect $200).", CardEffect.MoveTo(0, collectGo = true)),
        Card("Go to Jail. Go directly to Jail.", CardEffect.GoToJail),
        Card("Advance to Illinois Avenue.", CardEffect.MoveTo(24, collectGo = true)),
        Card("Advance to St. Charles Place.", CardEffect.MoveTo(11, collectGo = true)),
        Card("Take a trip to Reading Railroad.", CardEffect.MoveTo(5, collectGo = true)),
        Card("Bank pays you dividend of $50.", CardEffect.Money(50)),
        Card("Pay poor tax of $15.", CardEffect.Money(-15)),
        Card("Your building loan matures. Collect $150.", CardEffect.Money(150)),
        Card("Go back 3 spaces.", CardEffect.MoveSteps(-3)),
        Card("Advance to the nearest Railroad.", CardEffect.MoveToNearest(SquareType.RAILROAD)),
        Card("Advance to the nearest Utility.", CardEffect.MoveToNearest(SquareType.UTILITY)),
        Card("You have been elected Chairman. Pay each player $50.", CardEffect.PayEachPlayer(50))
    )

    val communityCards: List<Card> = listOf(
        Card("Advance to GO (Collect $200).", CardEffect.MoveTo(0, collectGo = true)),
        Card("Bank error in your favor. Collect $200.", CardEffect.Money(200)),
        Card("Doctor's fees. Pay $50.", CardEffect.Money(-50)),
        Card("From sale of stock you get $50.", CardEffect.Money(50)),
        Card("Get Out of Jail Free.", CardEffect.None),
        Card("Go to Jail.", CardEffect.GoToJail),
        Card("Grand Opera Night. Collect $50 from each player.", CardEffect.CollectFromEachPlayer(50)),
        Card("Holiday Fund matures. Collect $100.", CardEffect.Money(100)),
        Card("Income tax refund. Collect $20.", CardEffect.Money(20)),
        Card("Life insurance matures. Collect $100.", CardEffect.Money(100)),
        Card("Pay hospital fees of $100.", CardEffect.Money(-100)),
        Card("Pay school fees of $50.", CardEffect.Money(-50)),
        Card("Receive $25 consultancy fee.", CardEffect.Money(25)),
        Card("You inherit $100.", CardEffect.Money(100))
    )
}
