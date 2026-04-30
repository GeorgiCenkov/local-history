package com.example.localhistory.model.response

data class QuizDTO(
    val id: Long,
    val title: String,
    val landmarkId: Long,
    val questions: List<QuizQuestionDTO>
)
