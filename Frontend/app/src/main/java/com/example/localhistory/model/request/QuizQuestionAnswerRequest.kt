package com.example.localhistory.model.request

data class QuizQuestionAnswerRequest(
    val questionId: Long,
    val answer: String
)
