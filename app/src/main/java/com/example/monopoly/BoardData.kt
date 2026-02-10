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
        BoardSquare("GO", "GO", SquareType.GO),
        BoardSquare("Mediterranean Avenue", "Med Ave", SquareType.PROPERTY, cost = 60, rent = 2),
        BoardSquare("Community Chest", "Chest", SquareType.COMMUNITY_CHEST),
        BoardSquare("Baltic Avenue", "Baltic", SquareType.PROPERTY, cost = 60, rent = 4),
        BoardSquare("Income Tax", "Income Tax", SquareType.TAX, tax = 200),
        BoardSquare("Reading Railroad", "Reading RR", SquareType.RAILROAD, cost = 200),
        BoardSquare("Oriental Avenue", "Oriental", SquareType.PROPERTY, cost = 100, rent = 6),
        BoardSquare("Chance", "Chance", SquareType.CHANCE),
        BoardSquare("Vermont Avenue", "Vermont", SquareType.PROPERTY, cost = 100, rent = 6),
        BoardSquare("Connecticut Avenue", "Connecticut", SquareType.PROPERTY, cost = 120, rent = 8),
        BoardSquare("Jail / Just Visiting", "Jail", SquareType.JAIL),
        BoardSquare("St. Charles Place", "St. Charles", SquareType.PROPERTY, cost = 140, rent = 10),
        BoardSquare("Electric Company", "Electric", SquareType.UTILITY, cost = 150),
        BoardSquare("States Avenue", "States", SquareType.PROPERTY, cost = 140, rent = 10),
        BoardSquare("Virginia Avenue", "Virginia", SquareType.PROPERTY, cost = 160, rent = 12),
        BoardSquare("Pennsylvania Railroad", "Penn RR", SquareType.RAILROAD, cost = 200),
        BoardSquare("St. James Place", "St. James", SquareType.PROPERTY, cost = 180, rent = 14),
        BoardSquare("Community Chest", "Chest", SquareType.COMMUNITY_CHEST),
        BoardSquare("Tennessee Avenue", "Tennessee", SquareType.PROPERTY, cost = 180, rent = 14),
        BoardSquare("New York Avenue", "New York", SquareType.PROPERTY, cost = 200, rent = 16),
        BoardSquare("Free Parking", "Free Park", SquareType.FREE_PARKING),
        BoardSquare("Kentucky Avenue", "Kentucky", SquareType.PROPERTY, cost = 220, rent = 18),
        BoardSquare("Chance", "Chance", SquareType.CHANCE),
        BoardSquare("Indiana Avenue", "Indiana", SquareType.PROPERTY, cost = 220, rent = 18),
        BoardSquare("Illinois Avenue", "Illinois", SquareType.PROPERTY, cost = 240, rent = 20),
        BoardSquare("B. & O. Railroad", "B&O RR", SquareType.RAILROAD, cost = 200),
        BoardSquare("Atlantic Avenue", "Atlantic", SquareType.PROPERTY, cost = 260, rent = 22),
        BoardSquare("Ventnor Avenue", "Ventnor", SquareType.PROPERTY, cost = 260, rent = 22),
        BoardSquare("Water Works", "Water Works", SquareType.UTILITY, cost = 150),
        BoardSquare("Marvin Gardens", "Marvin", SquareType.PROPERTY, cost = 280, rent = 24),
        BoardSquare("Go To Jail", "Go To Jail", SquareType.GO_TO_JAIL),
        BoardSquare("Pacific Avenue", "Pacific", SquareType.PROPERTY, cost = 300, rent = 26),
        BoardSquare("North Carolina Avenue", "N. Carolina", SquareType.PROPERTY, cost = 300, rent = 26),
        BoardSquare("Community Chest", "Chest", SquareType.COMMUNITY_CHEST),
        BoardSquare("Pennsylvania Avenue", "Penn Ave", SquareType.PROPERTY, cost = 320, rent = 28),
        BoardSquare("Short Line", "Short Line", SquareType.RAILROAD, cost = 200),
        BoardSquare("Chance", "Chance", SquareType.CHANCE),
        BoardSquare("Park Place", "Park Place", SquareType.PROPERTY, cost = 350, rent = 35),
        BoardSquare("Luxury Tax", "Luxury Tax", SquareType.TAX, tax = 75),
        BoardSquare("Boardwalk", "Boardwalk", SquareType.PROPERTY, cost = 400, rent = 50)
    )
}
