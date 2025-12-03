package com.example.monopoly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PlayerSetupActivity : AppCompatActivity() {

    private lateinit var playerCountLayout: LinearLayout
    private lateinit var tokenSelectionContainer: FrameLayout
    private lateinit var btnConfirmCount: Button

    // POLA DLA PRZYCISKÓW LICZBY GRACZY
    private lateinit var btnPlayers2: Button
    private lateinit var btnPlayers3: Button
    private lateinit var btnPlayers4: Button

    // ZMODYFIKOWANA LISTA DOSTĘPNYCH PIONKÓW W USTALONEJ KOLEJNOŚCI
    // Gracz 1: silver_car_token
    // Gracz 2: ship_token
    // Gracz 3: hat_token
    // Gracz 4: dog_token
    private val availableTokens = listOf("silver_car_token", "ship_token", "hat_token", "dog_token")
    private var numberOfPlayers: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_setup)

        // Elementy Layoutu
        playerCountLayout = findViewById(R.id.player_count_layout)
        tokenSelectionContainer = findViewById(R.id.token_selection_container)
        btnConfirmCount = findViewById(R.id.btn_confirm_count)

        // INICJALIZACJA PÓL PRZYCISKÓW I LISTENERY
        btnPlayers2 = findViewById<Button>(R.id.btn_players_2).apply { setOnClickListener { selectPlayerCount(2) } }
        btnPlayers3 = findViewById<Button>(R.id.btn_players_3).apply { setOnClickListener { selectPlayerCount(3) } }
        btnPlayers4 = findViewById<Button>(R.id.btn_players_4).apply { setOnClickListener { selectPlayerCount(4) } }

        // Obsługa kliknięcia "Potwierdź liczbę graczy"
        btnConfirmCount.setOnClickListener {
            // Przechodzimy bezpośrednio do rozpoczęcia gry
            startGameAutomatically()
        }

        // Domyślny stan: Widok wyboru liczby graczy widoczny
        playerCountLayout.visibility = View.VISIBLE
        tokenSelectionContainer.visibility = View.GONE
    }

    // # ETAP 1: WYBÓR LICZBY GRACZY

    private fun resetPlayerCountButtonBorders() {
        btnPlayers2.setBackgroundResource(android.R.drawable.btn_default)
        btnPlayers3.setBackgroundResource(android.R.drawable.btn_default)
        btnPlayers4.setBackgroundResource(android.R.drawable.btn_default)
    }

    private fun selectPlayerCount(count: Int) {
        // Logika walidacji
        if (count > availableTokens.size) {
            Toast.makeText(this, "Maksymalna liczba graczy to ${availableTokens.size}!", Toast.LENGTH_LONG).show()
            return
        }

        numberOfPlayers = count

        resetPlayerCountButtonBorders()

        val selectedButton = when (count) {
            2 -> btnPlayers2
            3 -> btnPlayers3
            4 -> btnPlayers4
            else -> null
        }

        // Ustawienie ramki
        selectedButton?.setBackgroundResource(R.drawable.border_black_selected)

        btnConfirmCount.isEnabled = true
        Toast.makeText(this, "Wybrano $count graczy. Kliknij Potwierdź, aby rozpocząć.", Toast.LENGTH_SHORT).show()
    }

    // # ETAP 2: AUTOROZPOZCZĘCIE GRY Z USTALONYMI PIONKAMI

    private fun startGameAutomatically() {
        if (numberOfPlayers == 0) return

        // 🟢 Wybieramy pionki zgodnie z ustaloną kolejnością
        // Bierze pierwsze N pionków z listy availableTokens
        val selectedTokenList = availableTokens.take(numberOfPlayers)

        // Uruchomienie gry i przekazanie listy pionków
        val intent = Intent(this, GameActivity::class.java).apply {
            putStringArrayListExtra("PLAYER_TOKENS", ArrayList(selectedTokenList))
            putExtra("NUMBER_OF_PLAYERS", numberOfPlayers)
        }
        startActivity(intent)
        finish()
    }
}