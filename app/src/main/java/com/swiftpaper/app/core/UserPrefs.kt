package com.swiftpaper.app.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "swiftpaper_prefs")

class UserPrefs(private val context: Context) {
    private val isProKey = booleanPreferencesKey("is_pro")
    private val lastInterstitialKey = longPreferencesKey("last_interstitial_at")
    private val lastAppOpenKey = longPreferencesKey("last_app_open_at")
    private val sessionHdUnlockKey = booleanPreferencesKey("session_hd_unlock")

    val isPro: Flow<Boolean> = context.dataStore.data.map { it[isProKey] == true }

    val lastInterstitialAt: Flow<Long> = context.dataStore.data.map { it[lastInterstitialKey] ?: 0L }

    val lastAppOpenAt: Flow<Long> = context.dataStore.data.map { it[lastAppOpenKey] ?: 0L }

    /** One-shot HD / no-watermark unlock from rewarded ad (cleared after next export). */
    val sessionHdUnlock: Flow<Boolean> =
        context.dataStore.data.map { it[sessionHdUnlockKey] == true }

    suspend fun setPro(value: Boolean) {
        context.dataStore.edit { it[isProKey] = value }
    }

    suspend fun setLastInterstitialAt(epochMs: Long) {
        context.dataStore.edit { it[lastInterstitialKey] = epochMs }
    }

    suspend fun setLastAppOpenAt(epochMs: Long) {
        context.dataStore.edit { it[lastAppOpenKey] = epochMs }
    }

    suspend fun setSessionHdUnlock(value: Boolean) {
        context.dataStore.edit { it[sessionHdUnlockKey] = value }
    }
}
