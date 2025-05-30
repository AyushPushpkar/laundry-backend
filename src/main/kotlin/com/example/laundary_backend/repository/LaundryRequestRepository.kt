package com.example.laundary_backend.repository

import com.example.laundary_backend.entity.LaundryRequestEntity
import com.example.laundary_backend.entity.RequestStatus
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface LaundryRequestRepository : MongoRepository<LaundryRequestEntity, String> {
    fun findByVendorId(vendorId: String): List<LaundryRequestEntity>
    fun findByStatus(status: RequestStatus): List<LaundryRequestEntity>
    fun findByUserId(userId: String): List<LaundryRequestEntity>
}

