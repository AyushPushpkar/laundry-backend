package com.example.laundary_backend.service

import com.example.laundary_backend.dto.LaundryRequestDTO
import com.example.laundary_backend.repository.LaundryRequestRepository
import com.example.laundary_backend.utils.mapper.LaundryRequestMapper
import org.springframework.stereotype.Service
import com.example.laundary_backend.entity.RequestStatus
import com.example.laundary_backend.repository.UserRepository
import com.example.laundary_backend.repository.VendorRepository
import com.example.laundary_backend.utils.exception.AccessDeniedException
import com.example.laundary_backend.utils.exception.ResourceNotFoundException
import java.util.UUID

@Service
class LaundryRequestServiceImpl(
    private val vendorRepository: VendorRepository,
    private val userRepository: UserRepository,
    private val requestRepository: LaundryRequestRepository,
    private val requestMapper: LaundryRequestMapper
) : LaundryRequestService {

    override fun getUserRequests(userId: String): List<LaundryRequestDTO> {

        val requests = requestRepository.findByUserId(userId)
        return requests.map { requestMapper.fromEntity(it) }
    }

    override fun createRequest(requestDTO: LaundryRequestDTO , userEmail: String): LaundryRequestDTO {

        val user = userRepository.findByEmail(userEmail) ?: throw  ResourceNotFoundException("User not found for email: $userEmail")

        // Validate vendorId
        val vendor = vendorRepository.findById(requestDTO.vendorId)
            .orElseThrow { ResourceNotFoundException("Vendor not found with ID: ${requestDTO.vendorId}") }

        val requestWithId = if (requestDTO.id == null) {
            requestDTO.copy(id = UUID.randomUUID().toString())
        } else {
            requestDTO
        }

        val request = requestMapper.toEntity(requestWithId)
        val savedRequest = requestRepository.save(request)
        return requestMapper.fromEntity(savedRequest)
    }

    override fun updateRequestStatus(requestId: String, vendorId: String , status: RequestStatus): LaundryRequestDTO {


        val request = requestRepository.findById(requestId)
            .orElseThrow { RuntimeException("Request not found!") }

        if (request.vendorId != vendorId) {
            throw AccessDeniedException("You do not have permission to update this request")
        }

        request.status = status
        val updatedRequest = requestRepository.save(request)
        return requestMapper.fromEntity(updatedRequest)
    }

    override fun deleteRequest(requestId: String, userId: String) {
        val request = requestRepository.findById(requestId)
            .orElseThrow { ResourceNotFoundException("Laundry request not found with ID: $requestId") }

        // Check if the request belongs to the user
        if (request.userId != userId) {
            throw AccessDeniedException("You do not have permission to delete this request")
        }

        requestRepository.deleteById(requestId)
    }


    override fun getPendingRequestsForVendor(vendorId: String , currentUserEmail: String): List<LaundryRequestDTO> {

        val user = userRepository.findByEmail(currentUserEmail) ?: throw  ResourceNotFoundException("User not found for email: $currentUserEmail")
        if(vendorId != user.id) throw AccessDeniedException("You do not have permission to view this request")

        val requests = requestRepository.findByVendorId(vendorId)
            .filter { it.status == RequestStatus.PENDING }

        return requests.map { requestMapper.fromEntity(it) }
    }
}
