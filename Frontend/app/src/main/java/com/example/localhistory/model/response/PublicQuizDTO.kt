package com.example.localhistory.model.response

data class PublicQuizDTO(
    val id: Long,
    val title: String,
    val landmarkId: Long,
    val alreadyCompleted: Boolean = false, // tells if the authenticated user has completed the quiz
    val questions: List<PublicQuizQuestionDTO>
)
