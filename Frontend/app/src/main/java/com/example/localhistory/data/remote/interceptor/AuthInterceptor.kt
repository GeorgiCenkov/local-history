package com.example.localhistory.data.remote.interceptor

import com.example.localhistory.data.datastore.AuthDataStore
import com.example.localhistory.model.request.RefreshTokenRequest
import com.example.localhistory.model.response.AuthResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class AuthInterceptor(
    private val authDataStore: AuthDataStore,
    private val gson: Gson,
    private val baseUrl: String
) : Interceptor {

    private val refreshLock = Any()
    private val refreshClient = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = runBlocking {
            authDataStore.accessToken.first()
        }

        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()

        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(request.build())
        if (response.code != 401 || originalRequest.url.encodedPath.endsWith("/api/auth/refresh")) {
            return response
        }

        // Only one request should refresh at a time. The others reuse the token
        // that was saved while they were waiting instead of racing the backend.
        val refreshedToken = refreshAccessToken(token)
        if (refreshedToken == null) {
            runBlocking { authDataStore.clear() }
            return response
        }

        response.close()
        return chain.proceed(
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $refreshedToken")
                .build()
        ).also { retryResponse ->
            if (retryResponse.code == 401) {
                runBlocking { authDataStore.clear() }
            }
        }
    }

    private fun refreshAccessToken(accessTokenThatFailed: String?): String? = synchronized(refreshLock) {
        runBlocking {
            val currentAccessToken = authDataStore.accessToken.first()
            if (currentAccessToken != null && currentAccessToken != accessTokenThatFailed) {
                return@runBlocking currentAccessToken
            }

            // The refresh token is the last piece of trust we have. If the
            // backend rejects it, the saved session is cleared and App() shows login.
            val refreshToken = authDataStore.refreshToken.first() ?: return@runBlocking null
            val refreshResponse = executeRefresh(refreshToken) ?: return@runBlocking null
            authDataStore.saveAuth(refreshResponse)
            refreshResponse.token.jwtToken
        }
    }

    private fun executeRefresh(refreshToken: String): AuthResponse? {
        val body = gson.toJson(RefreshTokenRequest(refreshToken)).toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url("${baseUrl.trimEnd('/')}/api/auth/refresh")
            .post(body)
            .build()

        return try {
            refreshClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return null

                val responseBody = response.body?.string() ?: return null
                gson.fromJson(responseBody, AuthResponse::class.java)
            }
        } catch (_: Exception) {
            // A failed refresh means the saved session is no longer trustworthy.
            null
        }
    }
}
