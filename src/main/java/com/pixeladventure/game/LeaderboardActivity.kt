package com.pixeladventure.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pixeladventure.game.databinding.ActivityLeaderboardBinding

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val gamePrefs by lazy { GamePreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLeaderboard()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadLeaderboard() {
        val highScore = gamePrefs.getHighScore()
        val totalGames = gamePrefs.getTotalGamesPlayed()

        binding.highScoreText.text = "Highest Score: \$highScore"
        binding.totalGamesText.text = "Total Games Played: \$totalGames"

        val avgScore = if (totalGames > 0) highScore / totalGames else 0
        binding.averageScoreText.text = "Average Score: \$avgScore"
    }
}