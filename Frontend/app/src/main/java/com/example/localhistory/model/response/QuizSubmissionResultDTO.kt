package com.example.localhistory.model.response

data class QuizSubmissionResultDTO(
    val quizId: Long,
    val totalQuestions: Int,
    val answeredQuestions: Int,
    val correctAnswers: Int,
    val awardedPoints: Int,
    val alreadyCompleted: Boolean = false,
    val results: List<QuizQuestionResultDTO>
)
