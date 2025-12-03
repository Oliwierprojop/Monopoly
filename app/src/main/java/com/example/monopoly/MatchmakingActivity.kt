package com.example.monopoly

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MatchmakingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ładuje widok z pliku activity_matchmaking.xml
        setContentView(R.layout.activity_matchmaking)

        val botsOption: View = findViewById(R.id.cl_bots)
        val playersOption: View = findViewById(R.id.cl_players)

        botsOption.setOnClickListener {
            //Zamiast startowania gry, wyświetlamy komunikat "w trakcie prac..."
            Toast.makeText(this, "W trakcie prac...", Toast.LENGTH_LONG).show()
        }

        playersOption.setOnClickListener {
            // Przejście do ekranu wyboru graczy
            val intent = Intent(this, PlayerSetupActivity::class.java)
            startActivity(intent)

            // Możemy zakończyć MatchmakingActivity, aby nie wracać do niego przyciskiem Wstecz
            // finish()
        }
    }
}