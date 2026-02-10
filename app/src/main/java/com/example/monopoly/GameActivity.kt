package com.example.monopoly

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var diceArea: LinearLayout
    private lateinit var diceOneImage: ImageView
    private lateinit var diceTwoImage: ImageView
    private lateinit var apiResultText: TextView
    private lateinit var settingsButton: ImageButton

    //POLA DLA WIDOKÓW PIONKÓW
    private lateinit var tokenView1: ImageView
    private lateinit var tokenView2: ImageView
    private lateinit var tokenView3: ImageView
    private lateinit var tokenView4: ImageView
    private lateinit var tokenViews: List<ImageView>
    private lateinit var activeTokenViews: List<ImageView>
    private lateinit var boardContainer: View
    private lateinit var moneyViews: List<TextView>

    //POLA DO OBSŁUGI DANYCH Z EKRANU WYBORU GRACZY
    private var playerTokensList: List<String> = emptyList()
    private var actualNumberOfPlayers: Int = 0

    // MAPOWANIE NAZW PIONKÓW DO ZASOBÓW PNG W FOLDERZE DRAWABLE


    private var isBotMode: Boolean = false
    private var currentPlayerIndex: Int = 0
    private var playerPositions: IntArray = intArrayOf()
    private var owners: IntArray = intArrayOf()
    private var playerMoney: IntArray = intArrayOf()
    private var isDialogOpen: Boolean = false
    private var boardPositions: List<Pair<Float, Float>> = emptyList()
    private var cellWidth: Float = 0f
    private var cellHeight: Float = 0f
    private val requestQueue by lazy { Volley.newRequestQueue(this) }
    private val chanceDeck = CardDeck(CardsData.chanceCards)
    private val communityDeck = CardDeck(CardsData.communityCards)

    private companion object {
        private const val BOARD_SIZE = 11

        private const val TAG_GAME_SETUP = "GAME_SETUP"
        private const val TAG_TOKEN_ERROR = "TOKEN_ERROR"
        private const val TAG_API_DICE = "API_DICE"

        private const val EXTRA_PLAYER_TOKENS = "PLAYER_TOKENS"
        private const val EXTRA_NUMBER_OF_PLAYERS = "NUMBER_OF_PLAYERS"
        private const val EXTRA_IS_BOT_MODE = "IS_BOT_MODE"

        private const val START_CASH = 1500
        private const val GO_REWARD = 200
        private const val RAILROAD_RENT = 25
        private const val UTILITY_RENT_SINGLE = 4
        private const val UTILITY_RENT_BOTH = 10

        // Token name to drawable id map.
        private val TOKEN_DRAWABLE_MAP = mapOf(
            "silver_car_token" to R.drawable.silver_car_token,
            "ship_token" to R.drawable.ship_token,
            "hat_token" to R.drawable.hat_token,
            "dog_token" to R.drawable.dog_token
        )

        private val DICE_IMAGES = intArrayOf(
            R.drawable.dice_one, R.drawable.dice_two, R.drawable.dice_three,
            R.drawable.dice_four, R.drawable.dice_five, R.drawable.dice_six
        )

        private val TOKEN_OFFSETS = listOf(
            Pair(-0.2f, -0.2f),
            Pair(0.2f, -0.2f),
            Pair(-0.2f, 0.2f),
            Pair(0.2f, 0.2f)
        )
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        boardContainer = findViewById(R.id.board_container)

        // ODBIERANIE DANYCH PIONKÓW Z EKRANU PlayerSetupActivity
        playerTokensList = intent.getStringArrayListExtra(EXTRA_PLAYER_TOKENS) ?: emptyList()
        actualNumberOfPlayers = intent.getIntExtra(EXTRA_NUMBER_OF_PLAYERS, 2)
        isBotMode = intent.getBooleanExtra(EXTRA_IS_BOT_MODE, false) // Odczytujemy tryb bota

        if (playerTokensList.isNotEmpty()) {
            actualNumberOfPlayers = playerTokensList.size
        }
        playerPositions = IntArray(actualNumberOfPlayers) { 0 }
        owners = IntArray(BoardData.SQUARE_COUNT) { -1 }
        playerMoney = IntArray(actualNumberOfPlayers) { START_CASH }

        Log.d(TAG_GAME_SETUP, "Gracze: $actualNumberOfPlayers, Pionki: $playerTokensList, Tryb Bot: $isBotMode")

        // Inicjalizacja widoków stałych
        diceArea = findViewById(R.id.dice_area)
        diceOneImage = findViewById(R.id.dice_one)
        diceTwoImage = findViewById(R.id.dice_two)
        apiResultText = findViewById(R.id.api_result_text)
        settingsButton = findViewById(R.id.btn_settings)
        // Linia z playerToken została usunięta

        // INICJALIZACJA WIDOKÓW PIONKÓW (NOWYCH)
        tokenView1 = findViewById(R.id.tokenView1)
        tokenView2 = findViewById(R.id.tokenView2)
        tokenView3 = findViewById(R.id.tokenView3)
        tokenView4 = findViewById(R.id.tokenView4)
        tokenViews = listOf(tokenView1, tokenView2, tokenView3, tokenView4)
        activeTokenViews = tokenViews.take(actualNumberOfPlayers)
        setupMoneyViews()

        // GŁÓWNA ZMIANA: WYWOŁANIE FUNKCJI WYŚWIETLAJĄCEJ PIONKI
        displayPlayerTokens()

        boardContainer.doOnLayout {
            setupBoardPositions()
            placeAllTokens()
        }

        // Listener otwierający okno ustawień
        settingsButton.setOnClickListener {
            showSettingsDialog()
        }

        diceArea.setOnClickListener {
            // Po kliknięciu RZUCAMY KOŚĆMI POPRZEZ API
            rollDiceUsingApi()
        }
    }

    //WYŚWIETLANIE PIONKÓW NA PLANSZY
    private fun displayPlayerTokens() {
        // Najpierw ukrywamy wszystkie 4 widoki, aby pokazać tylko te, które są potrzebne
        tokenViews.forEach { it.visibility = View.GONE }

        playerTokensList.forEachIndexed { index, tokenName ->
            if (index < tokenViews.size) {
                val drawableId = TOKEN_DRAWABLE_MAP[tokenName]

                if (drawableId != null) {
                    // Ustawienie zasobu PNG na widoku
                    tokenViews[index].setImageResource(drawableId)
                    // Upewnienie się, że widok jest widoczny
                    tokenViews[index].visibility = View.VISIBLE
                } else {
                    Log.e(TAG_TOKEN_ERROR, "Nie znaleziono zasobu dla nazwy pionka: $tokenName")
                }
            }
        }
    }


    // Board positioning (11x11)
    private fun setupBoardPositions() {
        if (boardContainer.width == 0 || boardContainer.height == 0) return

        cellWidth = boardContainer.width / BOARD_SIZE.toFloat()
        cellHeight = boardContainer.height / BOARD_SIZE.toFloat()
        boardPositions = buildBoardPath()
    }

    private fun buildBoardPath(): List<Pair<Float, Float>> {
        val positions = ArrayList<Pair<Float, Float>>(BoardData.SQUARE_COUNT)

        // Start: GO (dolny prawy rĹ‚g)
        positions.add(cellCenter(10, 10))

        // Dolny rzÄ…d: od prawej do lewej (bez startu)
        for (col in 9 downTo 0) {
            positions.add(cellCenter(col, 10))
        }

        // Lewa kolumna: od doĹ‚u do gĂłry (bez dolnego rogu)
        for (row in 9 downTo 0) {
            positions.add(cellCenter(0, row))
        }

        // GĂłrny rzÄ…d: od lewej do prawej (bez lewego rogu)
        for (col in 1..10) {
            positions.add(cellCenter(col, 0))
        }

        // Prawa kolumna: od gĂłry do doĹ‚u (bez gĂłrnego i dolnego rogu)
        for (row in 1..9) {
            positions.add(cellCenter(10, row))
        }

        return positions
    }

    private fun cellCenter(col: Int, row: Int): Pair<Float, Float> {
        val x = (col + 0.5f) * cellWidth
        val y = (row + 0.5f) * cellHeight
        return Pair(x, y)
    }

    private fun placeAllTokens() {
        if (boardPositions.isEmpty()) return
        for (playerIndex in activeTokenViews.indices) {
            moveTokenToPosition(playerIndex, playerPositions[playerIndex])
        }
    }

    private fun moveTokenToPosition(playerIndex: Int, positionIndex: Int) {
        val tokenView = activeTokenViews.getOrNull(playerIndex) ?: return
        val center = boardPositions.getOrNull(positionIndex) ?: return

        val offset = TOKEN_OFFSETS.getOrNull(playerIndex) ?: Pair(0f, 0f)
        val offsetX = offset.first * cellWidth
        val offsetY = offset.second * cellHeight

        val targetX = center.first + offsetX - (tokenView.width / 2f)
        val targetY = center.second + offsetY - (tokenView.height / 2f)

        tokenView.x = targetX
        tokenView.y = targetY
    }

    private fun setupMoneyViews() {
        moneyViews = listOf(
            findViewById(R.id.tv_player1_money),
            findViewById(R.id.tv_player2_money),
            findViewById(R.id.tv_player3_money),
            findViewById(R.id.tv_player4_money)
        )

        for (i in moneyViews.indices) {
            moneyViews[i].visibility = if (i < actualNumberOfPlayers) View.VISIBLE else View.GONE
        }
        updateMoneyUi()
    }

    private fun updateMoneyUi() {
        if (!::moneyViews.isInitialized) return
        val count = minOf(actualNumberOfPlayers, moneyViews.size)
        for (i in 0 until count) {
            moneyViews[i].text = "Gracz ${i + 1}: $${playerMoney[i]}"
        }
    }

    private fun calculateRent(square: BoardSquare, ownerIndex: Int, diceTotal: Int): Int {
        return when (square.type) {
            SquareType.PROPERTY -> square.rent ?: 0
            SquareType.RAILROAD -> {
                val railroads = countOwnedByType(ownerIndex, SquareType.RAILROAD)
                RAILROAD_RENT * railroads.coerceAtLeast(1)
            }
            SquareType.UTILITY -> {
                val utilities = countOwnedByType(ownerIndex, SquareType.UTILITY)
                val multiplier = if (utilities >= 2) UTILITY_RENT_BOTH else UTILITY_RENT_SINGLE
                multiplier * diceTotal
            }
            else -> 0
        }
    }

    private fun countOwnedByType(ownerIndex: Int, type: SquareType): Int {
        var count = 0
        for (i in owners.indices) {
            if (owners[i] == ownerIndex && BoardData.squares[i].type == type) {
                count++
            }
        }
        return count
    }

    private fun adjustMoney(playerIndex: Int, delta: Int) {
        playerMoney[playerIndex] += delta
        updateMoneyUi()
    }

    private fun transferMoney(fromPlayer: Int, toPlayer: Int, amount: Int) {
        if (amount <= 0) return
        playerMoney[fromPlayer] -= amount
        playerMoney[toPlayer] += amount
        updateMoneyUi()
    }

    private data class CardResult(val followUp: (() -> Unit)?)

    private fun applyCardEffect(
        playerIndex: Int,
        card: Card,
        currentPosition: Int,
        diceTotal: Int
    ): CardResult {
        var followUp: (() -> Unit)? = null
        when (val effect = card.effect) {
            is CardEffect.Money -> adjustMoney(playerIndex, effect.amount)
            is CardEffect.PayEachPlayer -> {
                val amount = effect.amount
                for (i in playerMoney.indices) {
                    if (i == playerIndex) continue
                    transferMoney(playerIndex, i, amount)
                }
            }
            is CardEffect.CollectFromEachPlayer -> {
                val amount = effect.amount
                for (i in playerMoney.indices) {
                    if (i == playerIndex) continue
                    transferMoney(i, playerIndex, amount)
                }
            }
            is CardEffect.MoveTo -> {
                val target = effect.index
                if (effect.collectGo || target < currentPosition) {
                    adjustMoney(playerIndex, GO_REWARD)
                }
                playerPositions[playerIndex] = target
                moveTokenToPosition(playerIndex, target)
                followUp = { showLandingDialog(playerIndex, BoardData.squares[target], target, false, diceTotal) }
            }
            is CardEffect.MoveSteps -> {
                val target = (currentPosition + effect.steps + BoardData.SQUARE_COUNT) % BoardData.SQUARE_COUNT
                playerPositions[playerIndex] = target
                moveTokenToPosition(playerIndex, target)
                followUp = { showLandingDialog(playerIndex, BoardData.squares[target], target, false, diceTotal) }
            }
            is CardEffect.MoveToNearest -> {
                val target = findNextSquareOfType(currentPosition, effect.type)
                if (target < currentPosition) {
                    adjustMoney(playerIndex, GO_REWARD)
                }
                playerPositions[playerIndex] = target
                moveTokenToPosition(playerIndex, target)
                followUp = { showLandingDialog(playerIndex, BoardData.squares[target], target, false, diceTotal) }
            }
            CardEffect.GoToJail -> {
                val target = BoardData.JAIL_INDEX
                playerPositions[playerIndex] = target
                moveTokenToPosition(playerIndex, target)
                followUp = { showLandingDialog(playerIndex, BoardData.squares[target], target, true, diceTotal) }
            }
            CardEffect.None -> Unit
        }
        return CardResult(followUp)
    }

    private fun findNextSquareOfType(startIndex: Int, type: SquareType): Int {
        var index = (startIndex + 1) % BoardData.SQUARE_COUNT
        while (index != startIndex) {
            if (BoardData.squares[index].type == type) {
                return index
            }
            index = (index + 1) % BoardData.SQUARE_COUNT
        }
        return startIndex
    }

    private fun showLandingDialog(
        playerIndex: Int,
        square: BoardSquare,
        positionIndex: Int,
        wentToJail: Boolean,
        diceTotal: Int
    ) {
        if (isDialogOpen) return

        isDialogOpen = true
        diceArea.isEnabled = false
        diceArea.isClickable = false

        val playerNumber = playerIndex + 1
        val title = "Gracz $playerNumber: ${square.name}"
        val builder = AlertDialog.Builder(this).setTitle(title)
        var followUp: (() -> Unit)? = null

        if (wentToJail) {
            builder.setMessage("Idziesz do więzienia!")
                .setPositiveButton("OK", null)
        } else {
            when (square.type) {
            SquareType.PROPERTY, SquareType.RAILROAD, SquareType.UTILITY -> {
                val owner = owners.getOrNull(positionIndex) ?: -1
                if (owner == -1) {
                    val costText = square.cost?.toString() ?: "-"
                    builder.setMessage("Cena: $costText.\nCzy chcesz kupić to pole?")
                        .setPositiveButton("Kup") { _, _ ->
                            val cost = square.cost ?: 0
                            if (playerMoney[playerIndex] >= cost) {
                                owners[positionIndex] = playerIndex
                                adjustMoney(playerIndex, -cost)
                                Toast.makeText(this, "Kupiono: ${square.name}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Brak środków na zakup.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("Nie", null)
                } else if (owner == playerIndex) {
                    builder.setMessage("To Twoje pole.\nStan konta: ${playerMoney[playerIndex]}.")
                        .setPositiveButton("OK", null)
                } else {
                    val rent = calculateRent(square, owner, diceTotal)
                    transferMoney(playerIndex, owner, rent)
                    builder.setMessage("Pole należy do gracza ${owner + 1}.\nCzynsz: $rent.\nStan konta: ${playerMoney[playerIndex]}.")
                        .setPositiveButton("OK", null)
                }
            }
            SquareType.TAX -> {
                val taxText = square.tax?.toString() ?: "-"
                val tax = square.tax ?: 0
                adjustMoney(playerIndex, -tax)
                builder.setMessage("Podatek do zapłaty: $taxText.")
                    .setPositiveButton("OK", null)
            }
            SquareType.CHANCE -> {
                val card = chanceDeck.draw()
                val cardResult = applyCardEffect(playerIndex, card, positionIndex, diceTotal)
                builder.setMessage("Szansa!\n${card.text}\nStan konta: ${playerMoney[playerIndex]}.")
                    .setPositiveButton("OK", null)
                followUp = cardResult.followUp
            }
            SquareType.COMMUNITY_CHEST -> {
                val card = communityDeck.draw()
                val cardResult = applyCardEffect(playerIndex, card, positionIndex, diceTotal)
                builder.setMessage("Kasa Społeczna!\n${card.text}\nStan konta: ${playerMoney[playerIndex]}.")
                    .setPositiveButton("OK", null)
                followUp = cardResult.followUp
            }
            SquareType.JAIL -> {
                builder.setMessage("Więzienie / Odwiedziny.")
                    .setPositiveButton("OK", null)
            }
            SquareType.FREE_PARKING -> {
                builder.setMessage("Darmowy parking.")
                    .setPositiveButton("OK", null)
            }
            SquareType.GO -> {
                builder.setMessage("Start.")
                    .setPositiveButton("OK", null)
            }
            SquareType.GO_TO_JAIL -> {
                builder.setMessage("Idziesz do więzienia!")
                    .setPositiveButton("OK", null)
            }
            }
        }

        val dialog = builder.create()
        dialog.setOnDismissListener {
            isDialogOpen = false
            diceArea.isEnabled = true
            diceArea.isClickable = true
            followUp?.invoke()
        }
        dialog.show()
    }

    // Settings dialog
    private fun showSettingsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_settings) // Ładowanie layoutu dialogu

        val volumeSeekBar = dialog.findViewById<SeekBar>(R.id.seek_bar_volume)
        val stopButton = dialog.findViewById<Button>(R.id.btn_stop_game)
        val closeButton = dialog.findViewById<Button>(R.id.btn_close_settings)

        // 1. Warunkowe wyświetlanie przycisku Stop Gry
        if (isBotMode) {
            stopButton.visibility = View.VISIBLE
        } else {
            // Ukrywamy przycisk 'Stop Gry', jeśli gramy z przekazywaniem telefonu
            stopButton.visibility = View.GONE
        }

        // 2. Obsługa Stop Gry
        stopButton.setOnClickListener {
            Toast.makeText(this, "Gra z botami zatrzymana! Powrót...", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Zamknij dialog
            // finish() // Zamykamy GameActivity, co prowadzi do poprzedniej
        }

        // 3. Obsługa Seek Bar
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // W tym miejscu dodalibyśmy logikę zmiany głośności
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 4. Obsługa przycisku Zamknij
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Funkcja wywołująca API do rzucania kośćmi (bez zmian)
    private fun rollDiceUsingApi() {

        val url = "https://www.randomnumberapi.com/api/v1.0/random?min=2&max=12&count=1"

        apiResultText.text = "Łączę się z API... Rzucanie kośćmi online..."

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { _ ->
                try {
                    // Ponieważ API może zwrócić cokolwiek, używamy lokalnego losowania
                    val totalRoll = rollLocally()

                    Log.d(TAG_API_DICE, "Wylosowano API (lokalnie): $totalRoll")

                    processRollResult(totalRoll)

                } catch (e: Exception) {
                    Toast.makeText(this, "Błąd parsowania API: Używam lokalnego losowania.", Toast.LENGTH_LONG).show()
                    processRollResult(rollLocally())
                }
            },
            { _ ->
                Toast.makeText(this, "Błąd połączenia API: Używam lokalnego losowania.", Toast.LENGTH_LONG).show()
                processRollResult(rollLocally())
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun rollLocally(): Int = Random.nextInt(2, 13)

    // Funkcja wykonująca ruch i aktualizację widoków (bez zmian)
    private fun processRollResult(totalRoll: Int) {
        // Rozdzielenie wyniku na dwie kości (dla wizualizacji)
        val roll1 = if (totalRoll <= 7) totalRoll / 2 else 6
        val roll2 = totalRoll - roll1

        // Aktualizacja widoków kości
        diceOneImage.setImageResource(DICE_IMAGES[roll1 - 1])
        diceTwoImage.setImageResource(DICE_IMAGES[roll2 - 1])

        // Logika ruchu pionka
        if (actualNumberOfPlayers <= 0) return

        val playerIndex = currentPlayerIndex
        val playerNumber = playerIndex + 1
        val oldPosition = playerPositions[playerIndex]
        var newPosition = (oldPosition + totalRoll) % BoardData.SQUARE_COUNT
        if (oldPosition + totalRoll >= BoardData.SQUARE_COUNT) {
            adjustMoney(playerIndex, GO_REWARD)
        }
        playerPositions[playerIndex] = newPosition
        moveTokenToPosition(playerIndex, newPosition)

        var landedSquare = BoardData.squares[newPosition]
        var wentToJail = false
        if (landedSquare.type == SquareType.GO_TO_JAIL) {
            wentToJail = true
            newPosition = BoardData.JAIL_INDEX
            playerPositions[playerIndex] = newPosition
            moveTokenToPosition(playerIndex, newPosition)
            landedSquare = BoardData.squares[newPosition]
        }

        // Wyświetlenie komunikatu o ruchu
        Toast.makeText(this, "Gracz $playerNumber: wyrzucono $totalRoll! Ruszasz o $totalRoll pól.", Toast.LENGTH_SHORT).show()

        currentPlayerIndex = (currentPlayerIndex + 1) % actualNumberOfPlayers

        // Zaktualizowanie tekstu na ekranie
        apiResultText.text = "Gracz $playerNumber: ruch o $totalRoll (${landedSquare.name}, pole $newPosition)\nTeraz kolej gracza ${currentPlayerIndex + 1}."

        showLandingDialog(playerIndex, landedSquare, newPosition, wentToJail, totalRoll)

        // Opcjonalnie: Zaktualizuj jeszcze raz, aby dodać finansowy smaczek (jeśli chcesz go zachować)
        // fetchFinancialSmaczek()
    }

}
