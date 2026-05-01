package com.example.localhistory.model.response

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class HomeworkAssignmentDTO(
    val id: Long,
    val homeworkId: Long,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val assignedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val landmarkId: Long,
    val landmarkTitle: String?,
    val requireVisit: Boolean,
    val requireQuiz: Boolean,
    val visitCompleted: Boolean,
    val quizCompleted: Boolean,
    val requirementsSatisfied: Boolean,
    val completed: Boolean,
    val overdue: Boolean,
    val student: User.Student?
)
