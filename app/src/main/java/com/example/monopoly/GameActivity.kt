package com.example.monopoly

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
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

    //POLA DO OBSŁUGI DANYCH Z EKRANU WYBORU GRACZY
    private var playerTokensList: List<String> = emptyList()
    private var actualNumberOfPlayers: Int = 0

    // MAPOWANIE NAZW PIONKÓW DO ZASOBÓW PNG W FOLDERZE DRAWABLE
    private val tokenDrawableMap = mapOf(
        "silver_car_token" to R.drawable.silver_car_token,
        "ship_token" to R.drawable.ship_token,
        "hat_token" to R.drawable.hat_token,
        "dog_token" to R.drawable.dog_token
    )

    private val diceImages = arrayOf(
        R.drawable.dice_one, R.drawable.dice_two, R.drawable.dice_three,
        R.drawable.dice_four, R.drawable.dice_five, R.drawable.dice_six
    )

    private var currentPosition: Int = 0
    private var isBotMode: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // ODBIERANIE DANYCH PIONKÓW Z EKRANU PlayerSetupActivity
        playerTokensList = intent.getStringArrayListExtra("PLAYER_TOKENS") ?: emptyList()
        actualNumberOfPlayers = intent.getIntExtra("NUMBER_OF_PLAYERS", 2)
        isBotMode = intent.getBooleanExtra("IS_BOT_MODE", false) // Odczytujemy tryb bota

        Log.d("GAME_SETUP", "Gracze: $actualNumberOfPlayers, Pionki: $playerTokensList, Tryb Bot: $isBotMode")

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

        // GŁÓWNA ZMIANA: WYWOŁANIE FUNKCJI WYŚWIETLAJĄCEJ PIONKI
        displayPlayerTokens()

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
        val tokenViews = listOf(tokenView1, tokenView2, tokenView3, tokenView4)

        // Najpierw ukrywamy wszystkie 4 widoki, aby pokazać tylko te, które są potrzebne
        tokenViews.forEach { it.visibility = View.GONE }

        playerTokensList.forEachIndexed { index, tokenName ->
            if (index < tokenViews.size) {
                val drawableId = tokenDrawableMap[tokenName]

                if (drawableId != null) {
                    // Ustawienie zasobu PNG na widoku
                    tokenViews[index].setImageResource(drawableId)
                    // Upewnienie się, że widok jest widoczny
                    tokenViews[index].visibility = View.VISIBLE
                } else {
                    Log.e("TOKEN_ERROR", "Nie znaleziono zasobu dla nazwy pionka: $tokenName")
                }
            }
        }
    }


    // Funkcja wyświetlająca panel ustawień
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

        val queue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Ponieważ API może zwrócić cokolwiek, używamy lokalnego losowania
                    val totalRoll = Random.nextInt(2, 13)

                    Log.d("API_DICE", "Wylosowano API (lokalnie): $totalRoll")

                    processRollResult(totalRoll)

                } catch (e: Exception) {
                    Toast.makeText(this, "Błąd parsowania API: Używam lokalnego losowania.", Toast.LENGTH_LONG).show()
                    processRollResult(Random.nextInt(2, 13))
                }
            },
            { error ->
                Toast.makeText(this, "Błąd połączenia API: Używam lokalnego losowania.", Toast.LENGTH_LONG).show()
                processRollResult(Random.nextInt(2, 13))
            }
        )
        queue.add(jsonObjectRequest)
    }

    // Funkcja wykonująca ruch i aktualizację widoków (bez zmian)
    private fun processRollResult(totalRoll: Int) {
        // Rozdzielenie wyniku na dwie kości (dla wizualizacji)
        val roll1 = if (totalRoll <= 7) totalRoll / 2 else 6
        val roll2 = totalRoll - roll1

        // Aktualizacja widoków kości
        diceOneImage.setImageResource(diceImages[roll1 - 1])
        diceTwoImage.setImageResource(diceImages[roll2 - 1])

        // Logika ruchu pionka
        currentPosition = (currentPosition + totalRoll) % 40

        // Wyświetlenie komunikatu o ruchu
        Toast.makeText(this, "Wyrzucono $totalRoll! Ruszasz o $totalRoll pól.", Toast.LENGTH_SHORT).show()

        // Zaktualizowanie tekstu na ekranie
        apiResultText.text = "Ruch: $totalRoll (Na polu nr: $currentPosition)\nKości rzucone!."

        // Opcjonalnie: Zaktualizuj jeszcze raz, aby dodać finansowy smaczek (jeśli chcesz go zachować)
        // fetchFinancialSmaczek()
    }

}