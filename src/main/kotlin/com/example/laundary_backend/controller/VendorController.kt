package com.example.laundary_backend.controller


import com.example.laundary_backend.dto.VendorDTO
import com.example.laundary_backend.service.VendorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/vendors")
class VendorController(private val vendorService: VendorService) {

    // ✅ 201 Created – when vendor profile is successfully registered
    @PostMapping("/register")
    fun registerVendorProfile(
        @RequestBody vendorDTO: VendorDTO,
        principal: Principal
    ): ResponseEntity<String> {
        val email = principal.name
        val response = vendorService.createVendorProfile(email, vendorDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    // ✅ 200 OK – when fetching all vendors
    @GetMapping
    fun getAllVendors(): ResponseEntity<List<VendorDTO>> {
        val vendors = vendorService.getAllVendors()
        return ResponseEntity.status(HttpStatus.OK).body(vendors)
    }

    // ✅ 200 OK – when vendor is saved successfully
    // ✅ 409 Conflict – if vendor already saved (optional improvement)
    @PreAuthorize("hasAnyRole('USER', 'VENDOR' , 'DEVELOPER' , 'ADMIN')")
    @PostMapping("/save/{vendorId}")
    fun saveVendor(
        @PathVariable vendorId: String,
        principal: Principal
    ): ResponseEntity<String> {
        val email = principal.name
        val success = vendorService.saveVendorForUser(email, vendorId)

        return if (success) {
            ResponseEntity.status(HttpStatus.OK).body("Vendor saved successfully!")
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).body("Vendor was already saved.")
        }
    }


    @DeleteMapping("/unsave/{vendorId}")
    fun unsaveVendor(@PathVariable vendorId: String, principal: Principal): ResponseEntity<String> {
        vendorService.unSaveVendorForUser(principal.name, vendorId)
        return ResponseEntity.status(HttpStatus.OK).body("Vendor removed from saved list!")
    }

    @GetMapping("/saved")
    fun getSavedVendors(principal: Principal): ResponseEntity<List<VendorDTO>> {
        val savedVendors = vendorService.getSavedVendors(principal.name)
        return ResponseEntity.status(HttpStatus.OK).body(savedVendors)
    }


    private fun getAuthenticatedUserEmail(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication.name // This should return the email (used as an identifier)
    }

}

