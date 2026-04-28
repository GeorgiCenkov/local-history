package com.example.localhistory.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://localhost:8080/api"

    // logs every request and response to Logcat — very useful during dev
    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        // TODO: add auth token interceptor here later:
        // .addInterceptor(AuthInterceptor(tokenStore))
        .build()

    val auth: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl("$BASE_URL/auth/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // JSON → Kotlin
            .build()
            .create(AuthService::class.java)
    }
}