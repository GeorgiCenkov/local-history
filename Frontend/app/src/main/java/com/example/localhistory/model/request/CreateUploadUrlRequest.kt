package com.example.localhistory.model.request

data class CreateUploadUrlRequest(
    val fileName: String,
    val contentType: String
)
