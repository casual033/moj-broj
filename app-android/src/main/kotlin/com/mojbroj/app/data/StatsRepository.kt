package com.mojbroj.app.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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
    val exactSolutions: Int = 0,
    val avgDistance: Float = 0f,
    val bestStreak: Int = 0,
    val avgSolveTimeSec: Float = 0f
)

class StatsRepository(private val context: Context) {
    private object Keys {
        val totalGames = intPreferencesKey("total_games")
        val exactSolutions = intPreferencesKey("exact_solutions")
        val avgDistance = floatPreferencesKey("avg_distance")
        val bestStreak = intPreferencesKey("best_streak")
        val currentStreak = intPreferencesKey("current_streak")
        val avgSolveTimeSec = floatPreferencesKey("avg_solve_time_sec")
    }

    val stats: Flow<PlayerStats> = context.dataStore.data.map { prefs ->
        PlayerStats(
            totalGames = prefs[Keys.totalGames] ?: 0,
            exactSolutions = prefs[Keys.exactSolutions] ?: 0,
            avgDistance = prefs[Keys.avgDistance] ?: 0f,
            bestStreak = prefs[Keys.bestStreak] ?: 0,
            avgSolveTimeSec = prefs[Keys.avgSolveTimeSec] ?: 0f
        )
    }

    suspend fun recordGame(distance: Int, isExact: Boolean, solveTimeSec: Int) {
        context.dataStore.edit { prefs ->
            val totalGames = prefs[Keys.totalGames] ?: 0
            val exactSolutions = prefs[Keys.exactSolutions] ?: 0
            val avgDistance = prefs[Keys.avgDistance] ?: 0f
            val avgSolveTimeSec = prefs[Keys.avgSolveTimeSec] ?: 0f
            val currentStreak = prefs[Keys.currentStreak] ?: 0
            val bestStreak = prefs[Keys.bestStreak] ?: 0

            val newTotal = totalGames + 1
            val newExact = if (isExact) exactSolutions + 1 else exactSolutions
            val newAvgDistance = ((avgDistance * totalGames) + distance) / newTotal
            val newAvgSolveTime = ((avgSolveTimeSec * totalGames) + solveTimeSec) / newTotal
            val newCurrentStreak = if (isExact) currentStreak + 1 else 0
            val newBestStreak = max(bestStreak, newCurrentStreak)

            prefs[Keys.totalGames] = newTotal
            prefs[Keys.exactSolutions] = newExact
            prefs[Keys.avgDistance] = newAvgDistance
            prefs[Keys.avgSolveTimeSec] = newAvgSolveTime
            prefs[Keys.currentStreak] = newCurrentStreak
            prefs[Keys.bestStreak] = newBestStreak
        }
    }
}
