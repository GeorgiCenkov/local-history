package com.example.localhistory.model.response

data class QuizSubmissionResultDTO(
    val quizId: Long,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val results: List<QuizQuestionResultDTO>
)
