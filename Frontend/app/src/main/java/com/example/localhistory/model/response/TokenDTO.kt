package com.example.localhistory.model.response

import kotlinx.datetime.LocalDateTime

data class TokenDTO(
    val id: Int,
    val refreshToken: String,
    val expirationDate: LocalDateTime,
    val isRevoked: Boolean,
    val revokedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val jwtToken: String,
    val isExpired: Boolean
)
