package com.example.laundary_backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed

@Document(collection = "vendors")
data class VendorEntity(
    @Id
    val id: String? = null,

    val name: String,

    @Indexed(unique = true)
    val email: String,

    @Indexed(unique = true)
    val phone: String,

    val address: String ,

    val services: MutableList<ServiceType> = mutableListOf()
)

