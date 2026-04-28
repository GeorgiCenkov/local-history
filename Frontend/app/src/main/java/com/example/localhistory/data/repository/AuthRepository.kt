package com.example.localhistory.data.repository

import com.example.localhistory.data.remote.RetrofitInstance
import com.example.localhistory.model.request.LoginRequest
import com.example.localhistory.model.response.AuthResponse

// sealed class gives you success/error as types instead of exceptions
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

class AuthRepository {

    suspend fun login(email: String, password: String): AuthResult<AuthResponse> {
        return try {
            val response = RetrofitInstance.auth.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body() != null) {
                // API returned 2xx — unwrap the body and return it
                AuthResult.Success(response.body()!!)
            } else {
                // API returned 4xx/5xx — pull the error message
                AuthResult.Error(response.errorBody()?.string() ?: "Login failed")
            }
        } catch (e: Exception) {
            // network error, timeout, no internet etc.
            AuthResult.Error(e.message ?: "Network error")
        }
    }
}