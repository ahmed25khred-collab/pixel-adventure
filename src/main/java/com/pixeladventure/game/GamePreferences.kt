package com.pixeladventure.game

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("pixel_adventure", Context.MODE_PRIVATE)

    fun getHighScore(): Int = prefs.getInt("high_score", 0)

    fun saveHighScore(score: Int) {
        prefs.edit().putInt("high_score", score).apply()
    }

    fun getTotalGamesPlayed(): Int = prefs.getInt("total_games", 0)

    fun incrementGamesPlayed() {
        val current = getTotalGamesPlayed()
        prefs.edit().putInt("total_games", current + 1).apply()
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean("sound_enabled", true)

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }
}