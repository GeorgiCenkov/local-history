package com.example.localhistory.data.repository

import com.example.localhistory.data.remote.QuizService
import com.example.localhistory.model.request.QuizRequest
import com.example.localhistory.model.request.QuizSubmissionRequest
import com.example.localhistory.model.response.PublicQuizDTO
import com.example.localhistory.model.response.QuizDTO
import com.example.localhistory.model.response.QuizSubmissionResultDTO

class QuizRepository(
    private val api: QuizService
) {
    suspend fun getPublicQuizByLandmarkId(landmarkId: Long): LandmarkResult<PublicQuizDTO?> = runCatching {
        val response = api.getQuizByLandmarkId(landmarkId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else if (response.code() == 404) {
            LandmarkResult.Success(null)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getTeacherQuizzes(): LandmarkResult<List<QuizDTO>> = runCatching {
        val response = api.getTeacherQuizzes()
        if (response.isSuccessful) {
            LandmarkResult.Success(response.body().orEmpty())
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load quizzes")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getTeacherQuizById(id: Long): LandmarkResult<QuizDTO> = runCatching {
        val response = api.getTeacherQuizById(id)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun getTeacherQuizByLandmarkId(landmarkId: Long): LandmarkResult<QuizDTO?> = runCatching {
        val response = api.getTeacherQuizzes()
        if (response.isSuccessful) {
            LandmarkResult.Success(response.body().orEmpty().firstOrNull { it.landmarkId == landmarkId })
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not load quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun createQuiz(request: QuizRequest): LandmarkResult<QuizDTO> = runCatching {
        val response = api.createQuiz(request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not create quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun updateQuiz(id: Long, request: QuizRequest): LandmarkResult<QuizDTO> = runCatching {
        val response = api.updateQuiz(id, request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not update quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun deleteQuiz(id: Long): LandmarkResult<Unit> = runCatching {
        val response = api.deleteQuiz(id)
        if (response.isSuccessful) {
            LandmarkResult.Success(Unit)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not delete quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }

    suspend fun submitQuiz(id: Long, request: QuizSubmissionRequest): LandmarkResult<QuizSubmissionResultDTO> = runCatching {
        val response = api.submitQuiz(id, request)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            LandmarkResult.Success(body)
        } else {
            LandmarkResult.Error(response.errorBody()?.string() ?: "Could not submit quiz")
        }
    }.getOrElse {
        LandmarkResult.Error(it.message ?: "Network error")
    }
}
