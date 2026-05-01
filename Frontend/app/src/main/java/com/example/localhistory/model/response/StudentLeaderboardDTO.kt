package com.example.localhistory.model.response

data class StudentLeaderboardDTO(
    val rank: Int,
    val studentId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val level: Int,
    val points: Int,
    val pointsRequired: Int,
    val currentUser: Boolean
)
