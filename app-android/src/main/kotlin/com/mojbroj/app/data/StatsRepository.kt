package com.mojbroj.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max

private val Context.dataStore by preferencesDataStore(name = "moj_broj_stats")

data class PlayerStats(
    val totalGames: Int = 0,
    val validGames: Int = 0,
    val exactSolutions: Int = 0,
    val avgDistance: Float = 0f,
    val bestStreak: Int = 0,
    val avgSolveTimeSec: Float = 0f
)

class StatsRepository(private val context: Context) {
    private object Keys {
        val totalGames = intPreferencesKey("total_games")
        val validGames = intPreferencesKey("valid_games")
        val exactSolutions = intPreferencesKey("exact_solutions")
        val avgDistance = floatPreferencesKey("avg_distance")
        val bestStreak = intPreferencesKey("best_streak")
        val currentStreak = intPreferencesKey("current_streak")
        val avgSolveTimeSec = floatPreferencesKey("avg_solve_time_sec")
    }

    val stats: Flow<PlayerStats> = context.dataStore.data.map { prefs ->
        PlayerStats(
            totalGames = prefs[Keys.totalGames] ?: 0,
            validGames = prefs[Keys.validGames] ?: 0,
            exactSolutions = prefs[Keys.exactSolutions] ?: 0,
            avgDistance = prefs[Keys.avgDistance] ?: 0f,
            bestStreak = prefs[Keys.bestStreak] ?: 0,
            avgSolveTimeSec = prefs[Keys.avgSolveTimeSec] ?: 0f
        )
    }

    suspend fun recordGame(distance: Int, isExact: Boolean, isValid: Boolean, solveTimeSec: Int) {
        context.dataStore.edit { prefs ->
            val totalGames = prefs[Keys.totalGames] ?: 0
            val validGames = prefs[Keys.validGames] ?: 0
            val exactSolutions = prefs[Keys.exactSolutions] ?: 0
            val avgDistance = prefs[Keys.avgDistance] ?: 0f
            val avgSolveTimeSec = prefs[Keys.avgSolveTimeSec] ?: 0f
            val currentStreak = prefs[Keys.currentStreak] ?: 0
            val bestStreak = prefs[Keys.bestStreak] ?: 0

            prefs[Keys.totalGames] = totalGames + 1

            // Averages are computed over valid games only, so invalid or empty
            // submissions can't skew them.
            if (isValid) {
                val newValid = validGames + 1
                prefs[Keys.validGames] = newValid
                prefs[Keys.avgDistance] = ((avgDistance * validGames) + distance) / newValid
                prefs[Keys.avgSolveTimeSec] = ((avgSolveTimeSec * validGames) + solveTimeSec) / newValid
            }

            if (isExact) {
                prefs[Keys.exactSolutions] = exactSolutions + 1
            }

            val newCurrentStreak = if (isExact) currentStreak + 1 else 0
            prefs[Keys.currentStreak] = newCurrentStreak
            prefs[Keys.bestStreak] = max(bestStreak, newCurrentStreak)
        }
    }
}
