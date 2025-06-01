package com.example.laundary_backend.utils.mapper

import com.example.laundary_backend.dto.LaundryRequestDTO
import com.example.laundary_backend.entity.LaundryRequestEntity
import org.springframework.stereotype.Service

@Service
class LaundryRequestMapper : Mapper<LaundryRequestDTO, LaundryRequestEntity> {
    override fun fromEntity(entity: LaundryRequestEntity): LaundryRequestDTO {
        return LaundryRequestDTO(
            entity.id,
            entity.userId,
            entity.vendorId,
            entity.status
        )
    }

    override fun toEntity(dto: LaundryRequestDTO): LaundryRequestEntity {
        return LaundryRequestEntity(
            dto.id,
            dto.userId,
            dto.vendorId,
            dto.status
        )
    }
}
