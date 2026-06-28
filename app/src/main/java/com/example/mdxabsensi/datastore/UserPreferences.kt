package com.example.mdxabsensi.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "user_pref"
)

class UserPreferences(
    private val context: Context
) {

    companion object {

        val IS_LOGGED_IN =
            booleanPreferencesKey("is_logged_in")

        val NIK =
            stringPreferencesKey("nik")

        val NAMA =
            stringPreferencesKey("nama")

        val EMAIL =
            stringPreferencesKey("email")

        val FOTO =
            stringPreferencesKey("foto")

        val FOTO_LAST_UPDATED =
            longPreferencesKey("foto_last_updated")

        val THEME_MODE =
            stringPreferencesKey("theme_mode")
    }

    suspend fun saveUser(
        nik: String,
        nama: String,
        email: String,
        foto: String
    ) {

        context.dataStore.edit { pref ->

            pref[IS_LOGGED_IN] = true
            pref[NIK] = nik
            pref[NAMA] = nama
            pref[EMAIL] = email
            pref[FOTO] = foto
            pref[FOTO_LAST_UPDATED] = System.currentTimeMillis()
        }
    }

    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { pref ->
            pref[IS_LOGGED_IN] ?: false
        }

    val nik: Flow<String> =
        context.dataStore.data.map { pref ->
            pref[NIK] ?: ""
        }

    val nama: Flow<String> =
        context.dataStore.data.map { pref ->
            pref[NAMA] ?: ""
        }

    val email: Flow<String> =
        context.dataStore.data.map { pref ->
            pref[EMAIL] ?: ""
        }

    val foto: Flow<String> =
        context.dataStore.data.map { pref ->
            pref[FOTO] ?: ""
        }

    val fotoLastUpdated: Flow<Long> =
        context.dataStore.data.map { pref ->
            pref[FOTO_LAST_UPDATED] ?: 0L
        }

    val themeMode: Flow<String> =
        context.dataStore.data.map { pref ->
            pref[THEME_MODE] ?: "system"
        }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { pref ->
            pref[THEME_MODE] = mode
        }
    }

    suspend fun toggleTheme() {
        context.dataStore.edit { pref ->
            val current = pref[THEME_MODE] ?: "system"
            pref[THEME_MODE] = when (current) {
                "light" -> "dark"
                "dark" -> "system"
                else -> "light"
            }
        }
    }

    suspend fun logout() {

        context.dataStore.edit {

            it.clear()

        }
    }
}