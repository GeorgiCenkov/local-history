package com.example.localhistory.model.request

// what we send to the API on login
data class LoginRequest(
    val email:    String,
    val password: String
)
