package com.example.localhistory.model.request

// Request for creating a quiz
data class QuizRequest(
    val title: String,
    val landmarkId: Long,
    val questions: List<QuizQuestionRequest>
)
