package com.example.localhistory.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.localhistory.data.remote.adapter.LocalDateAdapter
import com.example.localhistory.model.response.AuthResponse
import com.example.localhistory.model.response.UserDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object AuthKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_DATA = stringPreferencesKey("user_data")
}

private val gson: Gson = GsonBuilder()
    .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
    .create()

data class AuthSession(
    val accessToken: String?,
    val refreshToken: String?,
    val user: UserDTO?
)

class AuthDataStore(private val context: Context) {

    private val store = context.dataStore

    // SAVE DATA
    suspend fun saveAuth(
        access: String,
        refresh: String,
        userId: Long,
        userData: UserDTO
    ) {
        store.edit { prefs ->
            prefs[AuthKeys.ACCESS_TOKEN] = access
            prefs[AuthKeys.REFRESH_TOKEN] = refresh
            prefs[AuthKeys.USER_ID] = userId.toString()
            prefs[AuthKeys.USER_DATA] = gson.toJson(userData)
        }
    }

    suspend fun saveAuth(response: AuthResponse) {
        saveAuth(
            access = response.token.jwtToken,
            refresh = response.token.refreshToken,
            userId = response.user.id,
            userData = response.user
        )
    }

    // READ ACCESS TOKEN
    val accessToken: Flow<String?> = store.data.map { prefs ->
        prefs[AuthKeys.ACCESS_TOKEN]
    }

    // READ REFRESH TOKEN
    val refreshToken: Flow<String?> = store.data.map { prefs ->
        prefs[AuthKeys.REFRESH_TOKEN]
    }

    // READ SERIALIZED USER DATA
    val user: Flow<UserDTO?> = store.data.map { prefs ->
        prefs[AuthKeys.USER_DATA]?.let {
            gson.fromJson(it, UserDTO::class.java)
        }
    }

    // READ COMPLETE AUTH SNAPSHOT
    val authSession: Flow<AuthSession> = store.data.map { prefs ->
        AuthSession(
            accessToken = prefs[AuthKeys.ACCESS_TOKEN],
            refreshToken = prefs[AuthKeys.REFRESH_TOKEN],
            user = prefs[AuthKeys.USER_DATA]?.let {
                gson.fromJson(it, UserDTO::class.java)
            }
        )
    }

    // CLEAR (logout)
    suspend fun clear() {
        store.edit { it.clear() }
    }
}
