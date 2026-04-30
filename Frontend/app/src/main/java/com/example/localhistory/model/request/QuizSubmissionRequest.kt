package com.example.localhistory.model.request

data class QuizSubmissionRequest(
    val answers: List<QuizQuestionAnswerRequest>
)
