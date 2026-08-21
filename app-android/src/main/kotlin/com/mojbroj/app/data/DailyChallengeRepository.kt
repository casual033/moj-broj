package com.mojbroj.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.TimeZone

private val Context.dailyDataStore by preferencesDataStore(name = "moj_broj_daily")

private const val MILLIS_PER_DAY = 86_400_000L

/** Current day index since epoch, adjusted to the device's local timezone. */
fun currentEpochDay(): Long {
    val nowMs = System.currentTimeMillis()
    return (nowMs + TimeZone.getDefault().getOffset(nowMs)) / MILLIS_PER_DAY
}

data class DailyChallengeState(
    val lastPlayedEpochDay: Long = -1L,
    val lastDistance: Int = -1,
    val lastExact: Boolean = false,
    val dailyStreak: Int = 0
) {
    fun playedToday(todayEpochDay: Long = currentEpochDay()): Boolean =
        lastPlayedEpochDay == todayEpochDay
}

class DailyChallengeRepository(private val context: Context) {
    private object Keys {
        val lastPlayedEpochDay = longPreferencesKey("daily_last_played_epoch_day")
        val lastDistance = intPreferencesKey("daily_last_distance")
        val lastExact = booleanPreferencesKey("daily_last_exact")
        val dailyStreak = intPreferencesKey("daily_streak")
    }

    val state: Flow<DailyChallengeState> = context.dailyDataStore.data.map { prefs ->
        DailyChallengeState(
            lastPlayedEpochDay = prefs[Keys.lastPlayedEpochDay] ?: -1L,
            lastDistance = prefs[Keys.lastDistance] ?: -1,
            lastExact = prefs[Keys.lastExact] ?: false,
            dailyStreak = prefs[Keys.dailyStreak] ?: 0
        )
    }

    suspend fun recordResult(epochDay: Long, distance: Int, isExact: Boolean) {
        context.dailyDataStore.edit { prefs ->
            val lastPlayed = prefs[Keys.lastPlayedEpochDay] ?: -1L
            if (lastPlayed == epochDay) return@edit

            val streak = prefs[Keys.dailyStreak] ?: 0
            val newStreak = if (epochDay == lastPlayed + 1) streak + 1 else 1

            prefs[Keys.lastPlayedEpochDay] = epochDay
            prefs[Keys.lastDistance] = distance
            prefs[Keys.lastExact] = isExact
            prefs[Keys.dailyStreak] = newStreak
        }
    }
}
