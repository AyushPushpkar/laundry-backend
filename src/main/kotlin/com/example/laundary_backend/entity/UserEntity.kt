package com.example.laundary_backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserEntity(
    @Id
    val id: String? = null,
    val name: String,

    @Indexed(unique = true)
    val email: String,

    val role: UserRole ,

     val password: String ,

    val savedVendors: MutableList<String> = mutableListOf()
){
    fun withEncodedPassword(encodedPassword: String) = this.copy(password = encodedPassword)

//    fun getPassword(): String = password
}





