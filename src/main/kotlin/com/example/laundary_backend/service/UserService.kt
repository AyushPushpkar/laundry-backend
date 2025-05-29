package com.example.laundary_backend.service


import com.example.laundary_backend.dto.UserDTO

interface UserService {
    val allUsers: List<UserDTO?>?

    fun getUserById(id: String?): UserDTO?
    fun getUserByEmail(email: String): UserDTO
    fun createUser(userDTO: UserDTO?): UserDTO?
}


