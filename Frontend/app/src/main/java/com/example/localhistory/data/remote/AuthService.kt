package com.example.localhistory.data.remote

import com.example.localhistory.model.request.LoginRequest
import com.example.localhistory.model.request.RegisterRequest
import com.example.localhistory.model.request.RefreshTokenRequest
import com.example.localhistory.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): Response<AuthResponse>
}
