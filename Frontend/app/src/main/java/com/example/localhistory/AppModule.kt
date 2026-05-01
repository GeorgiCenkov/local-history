package com.example.localhistory

import android.content.Context
import com.example.localhistory.data.datastore.AuthDataStore
import com.example.localhistory.data.remote.AuthService
import com.example.localhistory.data.remote.HomeworkService
import com.example.localhistory.data.remote.LandmarkService
import com.example.localhistory.data.remote.QuizService
import com.example.localhistory.data.remote.UploadService
import com.example.localhistory.data.remote.UserService
import com.example.localhistory.data.remote.adapter.LocalDateAdapter
import com.example.localhistory.data.remote.adapter.LocalDateTimeAdapter
import com.example.localhistory.data.remote.adapter.UserAdapter
import com.example.localhistory.data.remote.interceptor.AuthInterceptor
import com.example.localhistory.data.repository.AuthRepository
import com.example.localhistory.data.repository.HomeworkRepository
import com.example.localhistory.data.repository.ImageUploadRepository
import com.example.localhistory.data.repository.LandmarkRepository
import com.example.localhistory.data.repository.LocationRepository
import com.example.localhistory.data.repository.QuizRepository
import com.example.localhistory.data.repository.UploadHttpClient
import com.example.localhistory.data.repository.UserRepository
import com.example.localhistory.model.response.User
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
    private const val API_BASE_URL = "http://192.168.100.5:8080/"

    @Provides
    @Singleton
    fun provideAuthDataStore(@ApplicationContext context: Context): AuthDataStore =
        AuthDataStore(context)

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .registerTypeAdapter(User::class.java, UserAdapter())
        .create()

    @Provides
    @Singleton
    fun provideOkHttpClient(authDataStore: AuthDataStore, gson: Gson): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(authDataStore, gson, API_BASE_URL))
            .build()

    @Provides
    @Singleton
    @UploadHttpClient
    fun provideUploadOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideLandmarkService(retrofit: Retrofit): LandmarkService =
        retrofit.create(LandmarkService::class.java)

    @Provides
    @Singleton
    fun provideQuizService(retrofit: Retrofit): QuizService =
        retrofit.create(QuizService::class.java)

    @Provides
    @Singleton
    fun provideUploadService(retrofit: Retrofit): UploadService =
        retrofit.create(UploadService::class.java)

    @Provides
    @Singleton
    fun provideHomeworkService(retrofit: Retrofit): HomeworkService =
        retrofit.create(HomeworkService::class.java)

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthService,
        authDataStore: AuthDataStore
    ): AuthRepository = AuthRepository(api, authDataStore)

    @Provides
    @Singleton
    fun provideLandmarkRepository(api: LandmarkService): LandmarkRepository =
        LandmarkRepository(api)

    @Provides
    @Singleton
    fun provideQuizRepository(api: QuizService): QuizRepository =
        QuizRepository(api)

    @Provides
    @Singleton
    fun provideHomeworkRepository(api: HomeworkService): HomeworkRepository =
        HomeworkRepository(api)

    @Provides
    @Singleton
    fun provideUserRepository(api: UserService): UserRepository =
        UserRepository(api)

    @Provides
    @Singleton
    fun provideImageUploadRepository(
        @ApplicationContext context: Context,
        api: UploadService,
        @UploadHttpClient uploadHttpClient: OkHttpClient
    ): ImageUploadRepository = ImageUploadRepository(context, api, uploadHttpClient)

    @Provides
    @Singleton
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ): LocationRepository = LocationRepository(context)
}
