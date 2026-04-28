package com.example.localhistory.data.remote

import com.example.localhistory.model.request.LoginRequest
import com.example.localhistory.model.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// base url auth/
interface AuthService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // TODO: add more endpoints here
}