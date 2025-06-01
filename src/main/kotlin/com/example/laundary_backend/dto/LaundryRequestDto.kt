package com.example.laundary_backend.dto

import com.example.laundary_backend.entity.RequestStatus

data class LaundryRequestDTO(
    val id: String? = null,
    val userId: String,
    val vendorId: String,
    val status: RequestStatus = RequestStatus.PENDING
)
