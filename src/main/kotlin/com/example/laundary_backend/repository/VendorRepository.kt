package com.example.laundary_backend.repository

import com.example.laundary_backend.entity.UserEntity
import com.example.laundary_backend.entity.VendorEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VendorRepository : MongoRepository<VendorEntity, String> {
    fun findByIdIn(ids: List<String>): List<VendorEntity>
    fun findByEmail(email: String): VendorEntity?

}