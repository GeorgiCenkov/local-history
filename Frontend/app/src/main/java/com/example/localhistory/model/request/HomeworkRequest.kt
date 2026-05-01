package com.example.localhistory.model.request

import kotlinx.datetime.LocalDate

data class HomeworkRequest(
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val landmarkId: Long,
    val requireVisit: Boolean,
    val requireQuiz: Boolean
)
