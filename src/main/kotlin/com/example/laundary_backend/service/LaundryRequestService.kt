package com.example.laundary_backend.service

import com.example.laundary_backend.dto.LaundryRequestDTO
import com.example.laundary_backend.entity.RequestStatus

interface LaundryRequestService {
    fun getUserRequests(userId: String): List<LaundryRequestDTO>
    fun createRequest(requestDTO: LaundryRequestDTO, userEmail: String): LaundryRequestDTO
    fun updateRequestStatus(requestId: String, vendorId: String , status: RequestStatus): LaundryRequestDTO
    fun deleteRequest(requestId: String , userId: String)
    fun getPendingRequestsForVendor(vendorId: String , currentUserEmail: String): List<LaundryRequestDTO>
}

