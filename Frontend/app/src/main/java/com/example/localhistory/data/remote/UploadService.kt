package com.example.localhistory.data.remote

import com.example.localhistory.model.request.CreateUploadUrlRequest
import com.example.localhistory.model.response.CreateUploadUrlResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UploadService {
    @POST("api/uploads/signed-url")
    suspend fun createSignedUploadUrl(
        @Body request: CreateUploadUrlRequest
    ): Response<CreateUploadUrlResponse>
}
