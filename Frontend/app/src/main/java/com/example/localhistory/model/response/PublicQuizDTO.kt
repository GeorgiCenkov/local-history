package com.example.localhistory.model.response

data class PublicQuizDTO(
    val id: Long,
    val title: String,
    val landmarkId: Long,
    val questions: List<PublicQuizQuestionDTO>
)
