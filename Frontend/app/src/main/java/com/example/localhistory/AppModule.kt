package com.example.localhistory

import android.content.Context
import com.example.localhistory.data.datastore.AuthDataStore
import com.example.localhistory.data.remote.AuthService
import com.example.localhistory.data.remote.adapter.LocalDateAdapter
import com.example.localhistory.data.remote.adapter.LocalDateTimeAdapter
import com.example.localhistory.data.remote.interceptor.AuthInterceptor
import com.example.localhistory.data.repository.AuthRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): AuthDataStore =
        AuthDataStore(context)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(authDataStore: AuthDataStore): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(authDataStore))  // your auth interceptor
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.100.5:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthService,
        authDataStore: AuthDataStore
    ): AuthRepository = AuthRepository(api, authDataStore)
}