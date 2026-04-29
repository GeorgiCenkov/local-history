package com.example.localhistory.data.remote

import com.example.localhistory.model.request.LandmarkRequest
import com.example.localhistory.model.response.LandmarkDTO
import com.example.localhistory.model.response.LandmarkVisitDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface LandmarkService {

    @GET("api/landmarks/teacher")
    suspend fun getTeacherLandmarks(): Response<List<LandmarkDTO>>

    @GET("api/landmarks")
    suspend fun getAllLandmarks(): Response<List<LandmarkDTO>>

    @GET("api/landmarks/{id}")
    suspend fun getLandmarkById(
        @Path("id") id: Long
    ): Response<LandmarkDTO>

    @POST("api/landmarks")
    suspend fun createLandmark(
        @Body request: LandmarkRequest
    ): Response<LandmarkDTO>

    @PUT("api/landmarks/{id}")
    suspend fun updateLandmark(
        @Path("id") id: Long,
        @Body request: LandmarkRequest
    ): Response<LandmarkDTO>

    @DELETE("api/landmarks/{id}")
    suspend fun deleteLandmark(
        @Path("id") id: Long
    ): Response<Unit>

    @GET("api/landmarks/{id}/visits")
    suspend fun getVisitsForLandmark(
        @Path("id") id: Long
    ): Response<List<LandmarkVisitDTO>>

    @GET("api/landmarks/visits/{visitId}")
    suspend fun getVisitById(
        @Path("visitId") visitId: Long
    ): Response<LandmarkVisitDTO>

    @DELETE("api/landmarks/visits/{visitId}")
    suspend fun deleteVisit(
        @Path("visitId") visitId: Long
    ): Response<Unit>
}
