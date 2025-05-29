package com.example.laundary_backend.utils.mapper

import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.entity.UserEntity
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class UserMapper(private val passwordEncoder: PasswordEncoder) : Mapper<UserDTO, UserEntity> {
    override fun fromEntity(entity: UserEntity): UserDTO {
        return UserDTO(
            entity.id,
            entity.name,
            entity.email,
            entity.role ,
            savedVendors = entity.savedVendors
        )
    }

    override fun toEntity(dto: UserDTO): UserEntity {
        return UserEntity(
            dto.id,
            dto.name,
            dto.email,
            dto.role,
            password = dto.password?.let { passwordEncoder.encode(it) } ?: "",  // Encode only if provided
            savedVendors = dto.savedVendors.toMutableList()
        )
    }
}


