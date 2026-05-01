package com.example.localhistory.data.repository

import com.example.localhistory.data.remote.UserService
import com.example.localhistory.model.response.StudentLeaderboardDTO

sealed class UserResult<out T> {
    data class Success<T>(val data: T) : UserResult<T>()
    data class Error(val message: String) : UserResult<Nothing>()
}

class UserRepository(
    private val api: UserService
) {
    suspend fun getLeaderboard(search: String = ""): UserResult<List<StudentLeaderboardDTO>> = runCatching {
        val response = api.getLeaderboard(search)
        if (response.isSuccessful) {
            UserResult.Success(response.body().orEmpty())
        } else {
            UserResult.Error(response.errorBody()?.string() ?: "Could not load leaderboard")
        }
    }.getOrElse {
        UserResult.Error(it.message ?: "Network error")
    }

    suspend fun getMyLeaderboardRow(): UserResult<StudentLeaderboardDTO> = runCatching {
        val response = api.getMyLeaderboardRow()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            UserResult.Success(body)
        } else {
            UserResult.Error(response.errorBody()?.string() ?: "Could not find your rank")
        }
    }.getOrElse {
        UserResult.Error(it.message ?: "Network error")
    }
}
