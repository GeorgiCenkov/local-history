package com.example.localhistory.model.response

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class HomeworkDTO(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val dateCreated: LocalDateTime?,
    val requireVisit: Boolean,
    val requireQuiz: Boolean,
    val landmarkId: Long,
    val landmarkTitle: String?,
    val teacherId: Long,
    val assignmentsCount: Int
)
