package com.pixeladventure.game

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.pixeladventure.game.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var gameView: GameView
    private val gamePrefs by lazy { GamePreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        gameView = binding.gameView
        gameView.setGameListener(object : GameView.GameListener {
            override fun onScoreChange(score: Int) {
                binding.scoreText.text = "Score: \$score"
            }

            override fun onGameOver(finalScore: Int) {
                handleGameOver(finalScore)
            }
        })
    }

    private fun handleGameOver(finalScore: Int) {
        val highScore = gamePrefs.getHighScore()
        if (finalScore > highScore) {
            gamePrefs.saveHighScore(finalScore)
        }
        
        binding.gameOverLayout.visibility = View.VISIBLE
        binding.finalScoreText.text = "Final Score: \$finalScore"
        binding.restartButton.setOnClickListener {
            gameView.restartGame()
            binding.gameOverLayout.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }
}