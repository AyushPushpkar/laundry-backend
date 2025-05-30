package com.example.laundary_backend.auth

import com.example.laundary_backend.auth.refreshToken.RefreshTokenEntity
import com.example.laundary_backend.auth.refreshToken.RefreshTokenRepository
import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.entity.UserEntity
import com.example.laundary_backend.entity.UserRole
import com.example.laundary_backend.repository.UserRepository
import com.example.laundary_backend.utils.exception.InvalidRequestException
import com.example.laundary_backend.utils.exception.InvalidRoleException
import com.example.laundary_backend.utils.exception.UserAlreadyExistsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil ,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(userDTO: UserDTO): String {
        if (userRepository.findByEmail(userDTO.email) != null) {
            throw UserAlreadyExistsException("Email already exists!")
        }

        if (userDTO.password.isNullOrBlank()) {
            throw InvalidRequestException("Password cannot be null or empty")
        }

        if (userDTO.role != UserRole.USER && userDTO.role != UserRole.VENDOR) {
            throw InvalidRoleException("Only USER or VENDOR roles can be assigned at registration.")
        }

        val user = UserEntity(
            name = userDTO.name,
            email = userDTO.email,
            role = userDTO.role,
            password = passwordEncoder.encode(userDTO.password)
        )
        userRepository.save(user)

        return "User registered successfully!"
    }

    fun login(email: String, password: String): Map<String, String> {
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("Invalid credentials")

        if (!passwordEncoder.matches(password, user.password)) {
            throw RuntimeException("Invalid credentials")
        }

        val accessToken = jwtUtil.generateAccessToken(user.email, user.role)
        val refreshToken = jwtUtil.generateRefreshToken(user.email)

        // Store refresh token in DB (Remove old one if exists)
        refreshTokenRepository.deleteByUserEmail(user.email)
        refreshTokenRepository.save(
            RefreshTokenEntity(
                token = refreshToken,
                userEmail = user.email,
                expiryDate = Date(System.currentTimeMillis() + 2592000000) // 30 days
            )
        )

        return mapOf("accessToken" to accessToken, "refreshToken" to refreshToken)
    }

    fun refreshAccessToken(refreshToken: String): String {
        val storedToken = refreshTokenRepository.findByToken(refreshToken)
            ?: throw RuntimeException("Invalid refresh token")

        if (storedToken.expiryDate.toInstant().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken)
            throw RuntimeException("Refresh token expired, please log in again")
        }

        val user = userRepository.findByEmail(storedToken.userEmail)
            ?: throw RuntimeException("User not found")

        return jwtUtil.generateAccessToken(user.email, user.role)
    }

    fun logout(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw RuntimeException("User not found")

        refreshTokenRepository.deleteByUserEmail(user.email)
    }
}