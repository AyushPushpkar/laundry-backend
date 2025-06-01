package com.example.laundary_backend.controller

import com.example.laundary_backend.dto.LaundryRequestDTO
import com.example.laundary_backend.entity.RequestStatus
import com.example.laundary_backend.repository.UserRepository
import com.example.laundary_backend.service.LaundryRequestService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.security.Principal

@RestController
@RequestMapping("/requests")
class LaundryRequestController(
    private val requestService: LaundryRequestService ,
    private val userRepository: UserRepository,
    ) {

    //  Users can fetch their own requests
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    fun getUserRequests(principal: Principal): ResponseEntity<List<LaundryRequestDTO>> {
        val email = principal.name // Extract email from JWT
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val requests = requestService.getUserRequests(user.id!!)
        return ResponseEntity.ok(requests) // 200 OK
    }

    //  Users can create a new laundry request
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/create")
    fun createRequest(
        @RequestBody requestDTO: LaundryRequestDTO,
        principal: Principal
    ): ResponseEntity<LaundryRequestDTO> {
        println("Received request to create a new laundry request: $requestDTO")
        val userEmail = principal.name // Extract email from JWT (assuming you used email as username)
        val createdRequest = requestService.createRequest(requestDTO, userEmail)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    fun deleteRequest(
        @PathVariable id: String,
        principal: Principal
    ): ResponseEntity<String> {
        val email = principal.name
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        requestService.deleteRequest(id, user.id!!) // Pass actual userId to service
        return ResponseEntity.ok("Request deleted successfully!") // 200 OK
    }


    //  Vendors can accept requests
    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('VENDOR')")
    fun acceptRequest(
        @PathVariable id: String ,
        principal: Principal
    ): ResponseEntity<LaundryRequestDTO> {
        val email = principal.name
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val updated = requestService.updateRequestStatus(id, user.id!!, RequestStatus.ACCEPTED)
        return ResponseEntity.ok(updated) // 200 OK
    }


    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('VENDOR')")
    fun rejectRequest(
        @PathVariable id: String,
        principal: Principal
    ): ResponseEntity<LaundryRequestDTO> {
        val email = principal.name
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val updated = requestService.updateRequestStatus(id, user.id!!, RequestStatus.REJECTED)
        return ResponseEntity.ok(updated) // 200 OK
    }


    @PatchMapping("/{id}/pickup")
    @PreAuthorize("hasRole('VENDOR')")
    fun pickupRequest(
        @PathVariable id: String,
        principal: Principal
    ): ResponseEntity<LaundryRequestDTO> {
        val email = principal.name
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val updated = requestService.updateRequestStatus(id, user.id!!, RequestStatus.PICKED_UP)
        return ResponseEntity.ok(updated) // 200 OK
    }

    // Vendors can check their pending requests
    @GetMapping("/pending/{vendorId}")
    @PreAuthorize("hasRole('VENDOR')")
    fun getPendingRequestsForVendor(
        principal: Principal ,
        @PathVariable vendorId: String
    ): ResponseEntity<List<LaundryRequestDTO>> {
        val authentication = SecurityContextHolder.getContext().authentication
        println("Auth Object: $authentication")

        val email = principal.name

        val pendingRequests = requestService.getPendingRequestsForVendor(vendorId , email)
        println(vendorId)

        return ResponseEntity.ok(pendingRequests)
    }


    private fun getAuthenticatedUserId(principal: Principal): String {
        val user = userRepository.findByEmail(principal.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.id!!
    }


}
