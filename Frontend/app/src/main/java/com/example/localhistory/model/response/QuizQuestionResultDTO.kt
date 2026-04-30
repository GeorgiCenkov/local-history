package com.example.localhistory.model.response

data class QuizQuestionResultDTO(
    val questionId: Long,
    val submittedAnswer: String,
    val correctAnswer: String,
    val correct: Boolean
)
