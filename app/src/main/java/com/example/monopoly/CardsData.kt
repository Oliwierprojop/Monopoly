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
        Card("Przejdź na Start (odbierz $200).", CardEffect.MoveTo(0, collectGo = true)),
        Card("Idź do więzienia. Idź prosto do więzienia.", CardEffect.GoToJail),
        Card("Przejdź na Plac Wilsona.", CardEffect.MoveTo(24, collectGo = true)),
        Card("Przejdź na Płowiecką.", CardEffect.MoveTo(11, collectGo = true)),
        Card("Udaj się na Dworzec Zachodni.", CardEffect.MoveTo(5, collectGo = true)),
        Card("Bank wypłaca dywidendę $50.", CardEffect.Money(50)),
        Card("Zapłać podatek dla ubogich $15.", CardEffect.Money(-15)),
        Card("Twoja pożyczka budowlana dojrzewa. Odbierz $150.", CardEffect.Money(150)),
        Card("Cofnij się o 3 pola.", CardEffect.MoveSteps(-3)),
        Card("Przejdź do najbliższej kolei.", CardEffect.MoveToNearest(SquareType.RAILROAD)),
        Card("Przejdź do najbliższego zakładu użyteczności publicznej.", CardEffect.MoveToNearest(SquareType.UTILITY)),
        Card("Wybrano Cię przewodniczącym. Zapłać każdemu graczowi $50.", CardEffect.PayEachPlayer(50))
    )

    val communityCards: List<Card> = listOf(
        Card("Przejdź na Start (odbierz $200).", CardEffect.MoveTo(0, collectGo = true)),
        Card("Błąd banku na Twoją korzyść. Odbierz $200.", CardEffect.Money(200)),
        Card("Opłata lekarska. Zapłać $50.", CardEffect.Money(-50)),
        Card("Ze sprzedaży akcji otrzymujesz $50.", CardEffect.Money(50)),
        Card("Wyjdź z więzienia za darmo.", CardEffect.None),
        Card("Idź do więzienia.", CardEffect.GoToJail),
        Card("Wielka noc operowa. Pobierz $50 od każdego gracza.", CardEffect.CollectFromEachPlayer(50)),
        Card("Fundusz wakacyjny dojrzewa. Odbierz $100.", CardEffect.Money(100)),
        Card("Zwrot podatku dochodowego. Odbierz $20.", CardEffect.Money(20)),
        Card("Ubezpieczenie na życie dojrzewa. Odbierz $100.", CardEffect.Money(100)),
        Card("Zapłać rachunek za szpital $100.", CardEffect.Money(-100)),
        Card("Zapłać czesne $50.", CardEffect.Money(-50)),
        Card("Otrzymujesz $25 honorarium za konsultacje.", CardEffect.Money(25)),
        Card("Dziedziczysz $100.", CardEffect.Money(100))
    )
}
