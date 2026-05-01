package com.example.localhistory.data.remote

import com.example.localhistory.model.response.StudentLeaderboardDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("api/users/leaderboard")
    suspend fun getLeaderboard(
        @Query("search") search: String
    ): Response<List<StudentLeaderboardDTO>>

    @GET("api/users/leaderboard/me")
    suspend fun getMyLeaderboardRow(): Response<StudentLeaderboardDTO>
}
