package com.example.laundary_backend.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed

@Document(collection = "requests")
data class LaundryRequestEntity(
    @Id
    val id: String? = null,

    @Indexed
    val userId: String,

    @Indexed
    val vendorId: String,

    var status: RequestStatus = RequestStatus.PENDING
){
//    fun getId() : String? {
//        return id;
//    }
}




