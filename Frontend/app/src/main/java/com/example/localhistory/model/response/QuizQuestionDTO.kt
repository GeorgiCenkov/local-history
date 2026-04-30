package com.example.localhistory.model.response

data class QuizQuestionDTO(
    val id: Long,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
