package com.example.localhistory.model.response

data class PublicQuizQuestionDTO(
    val id: Long,
    val question: String,
    val options: List<String>
)
