package com.example.localhistory.data.remote

import com.example.localhistory.model.request.QuizRequest
import com.example.localhistory.model.request.QuizSubmissionRequest
import com.example.localhistory.model.response.PublicQuizDTO
import com.example.localhistory.model.response.QuizDTO
import com.example.localhistory.model.response.QuizSubmissionResultDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface QuizService {
    @GET("api/quizzes")
    suspend fun getAllQuizzes(): Response<List<PublicQuizDTO>>

    @GET("api/quizzes/landmark/{landmarkId}")
    suspend fun getQuizByLandmarkId(
        @Path("landmarkId") landmarkId: Long
    ): Response<PublicQuizDTO>

    @GET("api/quizzes/{id}")
    suspend fun getQuizById(
        @Path("id") id: Long
    ): Response<PublicQuizDTO>

    @GET("api/quizzes/teacher")
    suspend fun getTeacherQuizzes(): Response<List<QuizDTO>>

    @GET("api/quizzes/{id}/teacher")
    suspend fun getTeacherQuizById(
        @Path("id") id: Long
    ): Response<QuizDTO>

    @POST("api/quizzes")
    suspend fun createQuiz(
        @Body request: QuizRequest
    ): Response<QuizDTO>

    @PUT("api/quizzes/{id}")
    suspend fun updateQuiz(
        @Path("id") id: Long,
        @Body request: QuizRequest
    ): Response<QuizDTO>

    @DELETE("api/quizzes/{id}")
    suspend fun deleteQuiz(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/quizzes/{id}/submit")
    suspend fun submitQuiz(
        @Path("id") id: Long,
        @Body request: QuizSubmissionRequest
    ): Response<QuizSubmissionResultDTO>
}
