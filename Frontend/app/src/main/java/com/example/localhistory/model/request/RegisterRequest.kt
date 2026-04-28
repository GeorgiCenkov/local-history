package com.example.localhistory.model.request

import com.example.localhistory.model.response.Role

import kotlinx.datetime.LocalDate

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val birthDate: LocalDate,
    val role: Role
)