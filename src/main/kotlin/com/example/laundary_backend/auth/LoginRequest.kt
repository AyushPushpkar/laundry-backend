package com.example.laundary_backend.auth

data class LoginRequest(
    val email: String,
    val password: String
)