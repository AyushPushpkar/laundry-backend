package com.example.laundary_backend.dto

import com.example.laundary_backend.entity.ServiceType

data class VendorDTO(
    val id: String? = null,
    val name: String,
    val email: String,
    val phone: String,
    val address: String ,
    val services: MutableList<ServiceType> = mutableListOf()
)

