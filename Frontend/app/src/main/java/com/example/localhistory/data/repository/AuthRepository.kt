package com.example.localhistory.data.repository

import com.example.localhistory.data.datastore.AuthDataStore
import com.example.localhistory.data.remote.AuthService
import com.example.localhistory.model.request.LoginRequest
import com.example.localhistory.model.request.RegisterRequest
import com.example.localhistory.model.response.AuthResponse
import com.example.localhistory.model.response.Role
import kotlinx.datetime.LocalDate

// sealed class gives you success/error as types instead of exceptions
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository(
    private val api: AuthService,
    private val authDataStore: AuthDataStore
) {

    suspend fun login(email: String, password: String): AuthResult<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {

                val data = response.body() ?: return AuthResult.Error("Empty response")
                saveAuthData(data)

                AuthResult.Success(data)

            } else {
                val message = response.errorBody()?.string()
                AuthResult.Error(message ?: "Invalid email or password")
            }

        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        birthDate: LocalDate,
        role: Role
    ): AuthResult<AuthResponse> {
        return try {
            val response = api.register(
                RegisterRequest(firstName, lastName, email, password, birthDate, role)
            )
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                saveAuthData(data)
                AuthResult.Success(data)
            } else {
                AuthResult.Error(response.errorBody()?.string() ?: "Registration failed")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error")
        }
    }

    private suspend fun saveAuthData(response: AuthResponse) {
        authDataStore.saveAuth(
            access = response.token.jwtToken,
            refresh = response.token.refreshToken,
            userId = response.user.id,
            userData = response.user
        )
    }
}