package com.example.localhistory.model.response

data class CreateUploadUrlResponse(
    val path: String,
    val signedUrl: String,
    val publicUrl: String
)
