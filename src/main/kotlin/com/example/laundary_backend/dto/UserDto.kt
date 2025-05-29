package com.example.laundary_backend.dto

import com.example.laundary_backend.entity.UserRole

data class UserDTO(
    val id: String? = null,
    val name: String,
    val email: String,
    val role: UserRole,
    val password: String? = null , // Nullable because we don’t return it in responses
    val savedVendors: MutableList<String> = mutableListOf()
)


