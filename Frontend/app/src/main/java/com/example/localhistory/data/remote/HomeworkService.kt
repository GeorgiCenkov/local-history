package com.example.localhistory.data.remote

import com.example.localhistory.model.request.HomeworkAssignmentRequest
import com.example.localhistory.model.request.HomeworkRequest
import com.example.localhistory.model.response.HomeworkAssignmentDTO
import com.example.localhistory.model.response.HomeworkDTO
import com.example.localhistory.model.response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeworkService {
    @GET("api/homework/teacher")
    suspend fun getTeacherHomework(): Response<List<HomeworkDTO>>

    @POST("api/homework")
    suspend fun createHomework(
        @Body request: HomeworkRequest
    ): Response<HomeworkDTO>

    @PUT("api/homework/{id}")
    suspend fun updateHomework(
        @Path("id") id: Long,
        @Body request: HomeworkRequest
    ): Response<HomeworkDTO>

    @DELETE("api/homework/{id}")
    suspend fun deleteHomework(
        @Path("id") id: Long
    ): Response<Unit>

    @GET("api/homework/{id}/assignments")
    suspend fun getAssignments(
        @Path("id") id: Long
    ): Response<List<HomeworkAssignmentDTO>>

    @POST("api/homework/{id}/assignments")
    suspend fun assignHomework(
        @Path("id") id: Long,
        @Body request: HomeworkAssignmentRequest
    ): Response<List<HomeworkAssignmentDTO>>

    @GET("api/users/search")
    suspend fun searchStudents(
        @Query("search") search: String
    ): Response<List<User>>
}
