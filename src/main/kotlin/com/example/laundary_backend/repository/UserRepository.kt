package com.example.laundary_backend.repository

import com.example.laundary_backend.entity.UserEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : MongoRepository<UserEntity, String> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailIgnoreCase(email: String): Optional<UserEntity>
}

