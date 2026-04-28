package com.example.localhistory.model.response

import kotlinx.datetime.LocalDate

data class UserDTO(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val birthDate: LocalDate,
    val role: Role
)
