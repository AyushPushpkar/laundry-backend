package com.example.laundary_backend.service


import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.entity.UserEntity
import com.example.laundary_backend.repository.UserRepository
import com.example.laundary_backend.utils.mapper.UserMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository ,
    private val userMapper: UserMapper ,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override val allUsers: List<UserDTO>
        get() = userRepository.findAll().stream()
            .map { entity: UserEntity? -> userMapper.fromEntity(entity!!) }
            .toList()

    override fun getUserById(id: String?): UserDTO {
        val user = userRepository.findById(id!!)
            .orElseThrow { RuntimeException("User not found") }
        return userMapper.fromEntity(user)
    }

    override fun getUserByEmail(email: String): UserDTO {
        println("🔍 Searching for user by email: $email")  // Debugging log

        val user: UserEntity = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow { RuntimeException("User not found for email: $email") }

        println("✅ User found: ${user.email}")  // Log success
        return userMapper.fromEntity(user)
    }

    override fun createUser(userDTO: UserDTO?): UserDTO? {
        if (userDTO == null || userDTO.password.isNullOrBlank()) {
            throw IllegalArgumentException("UserDTO and password cannot be null or empty")
        }

        val user = userMapper.toEntity(userDTO)

        // Encode password safely
        val userWithEncodedPassword = user.copy(password = passwordEncoder.encode(userDTO.password))

        // Save user to database
        val savedUser = userRepository.save(userWithEncodedPassword)

        // Convert entity back to DTO
        return userMapper.fromEntity(savedUser)
    }


}