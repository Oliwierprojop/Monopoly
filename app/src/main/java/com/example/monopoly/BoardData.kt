package com.example.monopoly

enum class SquareType {
    GO,
    PROPERTY,
    RAILROAD,
    UTILITY,
    TAX,
    CHANCE,
    COMMUNITY_CHEST,
    JAIL,
    FREE_PARKING,
    GO_TO_JAIL
}

data class BoardSquare(
    val name: String,
    val label: String,
    val type: SquareType,
    val cost: Int? = null,
    val rent: Int? = null,
    val tax: Int? = null
)

object BoardData {
    const val SQUARE_COUNT = 40
    const val JAIL_INDEX = 10
    const val GO_TO_JAIL_INDEX = 30

    val squares: List<BoardSquare> = listOf(
        BoardSquare("Start", "Start", SquareType.GO),
        BoardSquare("Konopacka", "Konopacka", SquareType.PROPERTY, cost = 60, rent = 2),
        BoardSquare("Kasa Społeczna", "Kasa", SquareType.COMMUNITY_CHEST),
        BoardSquare("Stalowa", "Stalowa", SquareType.PROPERTY, cost = 60, rent = 4),
        BoardSquare("Podatek dochodowy", "Podatek", SquareType.TAX, tax = 200),
        BoardSquare("Dworzec Zachodni", "Zachodni", SquareType.RAILROAD, cost = 200),
        BoardSquare("Radzymińska", "Radzymińska", SquareType.PROPERTY, cost = 100, rent = 6),
        BoardSquare("Szansa", "Szansa", SquareType.CHANCE),
        BoardSquare("Jagiellońska", "Jagiellońska", SquareType.PROPERTY, cost = 100, rent = 6),
        BoardSquare("Targowa", "Targowa", SquareType.PROPERTY, cost = 120, rent = 8),
        BoardSquare("Więzienie / Odwiedziny", "Więzienie", SquareType.JAIL),
        BoardSquare("Płowiecka", "Płowiecka", SquareType.PROPERTY, cost = 140, rent = 10),
        BoardSquare("Elektrownia", "Elektrownia", SquareType.UTILITY, cost = 150),
        BoardSquare("Marsa", "Marsa", SquareType.PROPERTY, cost = 140, rent = 10),
        BoardSquare("Grochowska", "Grochowska", SquareType.PROPERTY, cost = 160, rent = 12),
        BoardSquare("Dworzec Gdański", "Gdański", SquareType.RAILROAD, cost = 200),
        BoardSquare("Obozowa", "Obozowa", SquareType.PROPERTY, cost = 180, rent = 14),
        BoardSquare("Kasa Społeczna", "Kasa", SquareType.COMMUNITY_CHEST),
        BoardSquare("Górczewska", "Górczewska", SquareType.PROPERTY, cost = 180, rent = 14),
        BoardSquare("Wolska", "Wolska", SquareType.PROPERTY, cost = 200, rent = 16),
        BoardSquare("Bezpłatny parking", "Parking", SquareType.FREE_PARKING),
        BoardSquare("Mickiewicza", "Mickiewicza", SquareType.PROPERTY, cost = 220, rent = 18),
        BoardSquare("Szansa", "Szansa", SquareType.CHANCE),
        BoardSquare("Słowackiego", "Słowackiego", SquareType.PROPERTY, cost = 220, rent = 18),
        BoardSquare("Plac Wilsona", "Wilsona", SquareType.PROPERTY, cost = 240, rent = 20),
        BoardSquare("Dworzec Wschodni", "Wschodni", SquareType.RAILROAD, cost = 200),
        BoardSquare("Świętokrzyska", "Świętokrzyska", SquareType.PROPERTY, cost = 260, rent = 22),
        BoardSquare("Krakowskie Przedmieście", "Krakowskie Prz.", SquareType.PROPERTY, cost = 260, rent = 22),
        BoardSquare("Wodociągi", "Wodociągi", SquareType.UTILITY, cost = 150),
        BoardSquare("Nowy Świat", "Nowy Świat", SquareType.PROPERTY, cost = 280, rent = 24),
        BoardSquare("Idź do więzienia", "Do więzienia", SquareType.GO_TO_JAIL),
        BoardSquare("Plac Trzech Krzyży", "Trzech Krzyży", SquareType.PROPERTY, cost = 300, rent = 26),
        BoardSquare("Marszałkowska", "Marszałkowska", SquareType.PROPERTY, cost = 300, rent = 26),
        BoardSquare("Kasa Społeczna", "Kasa", SquareType.COMMUNITY_CHEST),
        BoardSquare("Aleje Jerozolimskie", "Jerozolimskie", SquareType.PROPERTY, cost = 320, rent = 28),
        BoardSquare("Dworzec Centralny", "Centralny", SquareType.RAILROAD, cost = 200),
        BoardSquare("Szansa", "Szansa", SquareType.CHANCE),
        BoardSquare("Belwederska", "Belwederska", SquareType.PROPERTY, cost = 350, rent = 35),
        BoardSquare("Domiar podatkowy", "Domiar", SquareType.TAX, tax = 75),
        BoardSquare("Aleje Ujazdowskie", "Ujazdowskie", SquareType.PROPERTY, cost = 400, rent = 50)
    )
}
