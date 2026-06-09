package com.pixeladventure.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pixeladventure.game.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val gamePrefs by lazy { GamePreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setupUI()
        loadHighScore()
    }

    private fun setupUI() {
        binding.playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        binding.leaderboardButton.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        binding.settingsButton.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun loadHighScore() {
        val highScore = gamePrefs.getHighScore()
        binding.highScoreText.text = "High Score: \$highScore"
    }

    private fun showSettingsDialog() {
        // Settings implementation
    }

    override fun onResume() {
        super.onResume()
        loadHighScore()
    }
}