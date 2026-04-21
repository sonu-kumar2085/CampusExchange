package com.campusexchange.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "campus_exchange_prefs")

@Singleton
class TokenDataStore @Inject constructor(private val context: Context) {

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val USERNAME_KEY = stringPreferencesKey("username")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
        }
    }

    fun getAccessToken(): Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN_KEY] }

    fun getRefreshToken(): Flow<String?> = context.dataStore.data.map { it[REFRESH_TOKEN_KEY] }

    fun getUsername(): Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    fun isLoggedIn(): Flow<Boolean> = context.dataStore.data.map {
        !it[ACCESS_TOKEN_KEY].isNullOrBlank()
    }
}
