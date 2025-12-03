package com.example.monopoly

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val splashLayout: View = findViewById(R.id.splash_layout)

        splashLayout.setOnClickListener {
            // Logika przejścia do nowej aktywności
            val intent = Intent(this, MatchmakingActivity::class.java)
            startActivity(intent)

        }
    }
}