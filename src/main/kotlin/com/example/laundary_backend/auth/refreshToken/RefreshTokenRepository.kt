package com.example.laundary_backend.auth.refreshToken

import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository : MongoRepository<RefreshTokenEntity, String> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByUserEmail(userEmail: String)
    fun existsByUserEmail(userEmail: String): Boolean
    fun findByUserEmail(userEmail: String): RefreshTokenEntity?
}
