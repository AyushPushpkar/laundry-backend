package com.example.laundary_backend.auth.refreshToken

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.*

@Document(collection = "refresh_tokens")
data class RefreshTokenEntity(
    @Id val id: String? = null,
    @Field("token") val token: String,
    @Field("userEmail") val userEmail: String,
    @Field("expiryDate") val expiryDate: Date
)

