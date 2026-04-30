package com.example.localhistory.model.request

// Request for creating a quiz question
data class QuizQuestionRequest(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
