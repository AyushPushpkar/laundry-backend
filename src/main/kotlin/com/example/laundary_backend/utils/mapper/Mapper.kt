package com.example.laundary_backend.utils.mapper

interface Mapper<D, E> {
    fun fromEntity(entity: E): D
    fun toEntity(dto: D): E
}

