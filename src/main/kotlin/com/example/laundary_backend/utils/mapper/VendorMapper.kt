package com.example.laundary_backend.utils.mapper

import com.example.laundary_backend.dto.VendorDTO
import com.example.laundary_backend.entity.VendorEntity
import org.springframework.stereotype.Service

@Service
class VendorMapper : Mapper<VendorDTO, VendorEntity> {
    override fun fromEntity(entity: VendorEntity): VendorDTO {
        return VendorDTO(
            entity.id,
            entity.name,
            entity.email,
            entity.phone,
            entity.address,
            entity.services
        )
    }

    override fun toEntity(dto: VendorDTO): VendorEntity {
        return VendorEntity(
            dto.id,
            dto.name,
            dto.email,
            dto.phone,
            dto.address ,
            dto.services.toMutableList()
        )
    }
}
