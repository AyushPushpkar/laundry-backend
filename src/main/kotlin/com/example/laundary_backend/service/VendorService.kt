package com.example.laundary_backend.service

import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.dto.VendorDTO

interface VendorService {
    fun getAllVendors(): List<VendorDTO>
    fun saveVendorForUser(userEmail: String, vendorId: String) : Boolean
    fun unSaveVendorForUser(userEmail: String, vendorId: String)
    fun getSavedVendors(userEmail: String): List<VendorDTO>

    fun createVendorProfile(currentUserEmail: String, vendorDTO: VendorDTO): String
}