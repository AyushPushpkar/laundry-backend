package com.example.laundary_backend.service

import com.example.laundary_backend.dto.VendorDTO
import com.example.laundary_backend.entity.UserRole
import com.example.laundary_backend.entity.VendorEntity
import com.example.laundary_backend.repository.UserRepository
import com.example.laundary_backend.repository.VendorRepository
import com.example.laundary_backend.utils.exception.InvalidRoleException
import com.example.laundary_backend.utils.exception.UserNotFoundException
import com.example.laundary_backend.utils.exception.VendorAlreadyExistsException
import com.example.laundary_backend.utils.mapper.VendorMapper
import org.springframework.stereotype.Service

@Service
class VendorServiceImpl(
    private val vendorRepository: VendorRepository,
    private val userRepository: UserRepository,
    private val vendorMapper: VendorMapper
) : VendorService {

    override fun createVendorProfile(currentUserEmail: String, vendorDTO: VendorDTO): String {
        val user = userRepository.findByEmail(currentUserEmail)
            ?: throw UserNotFoundException("User not found")

        if (user.role != UserRole.VENDOR) {
            throw InvalidRoleException("Only users with VENDOR role can register as a vendor")
        }

        if (vendorRepository.findByEmail(currentUserEmail) != null) {
            throw VendorAlreadyExistsException("Vendor profile already exists for this user")
        }

        val vendor = VendorEntity(
            id = user.id,
            name = vendorDTO.name,
            email = currentUserEmail, // override to secure the source of truth
            phone = vendorDTO.phone,
            address = vendorDTO.address,
            services = vendorDTO.services
        )

        vendorRepository.save(vendor)

        return "Vendor profile registered successfully"
    }


    override fun getAllVendors(): List<VendorDTO> {
        return vendorRepository.findAll().map { vendorMapper.fromEntity(it) }
    }

    override fun saveVendorForUser(userEmail: String, vendorId: String): Boolean {
        val user = userRepository.findByEmail(userEmail) ?: throw RuntimeException("User not found")

        return if (!user.savedVendors.contains(vendorId)) {
            user.savedVendors.add(vendorId)
            userRepository.save(user)
            true // newly added
        } else {
            false // already exists
        }
    }

    override fun unSaveVendorForUser(userEmail: String, vendorId: String) {
        val user = userRepository.findByEmail(userEmail) ?: throw RuntimeException("User not found")


        if (user.savedVendors.contains(vendorId)) {
            user.savedVendors.remove(vendorId)
            userRepository.save(user)
        }
    }

    override fun getSavedVendors(userEmail: String): List<VendorDTO> {
        val user = userRepository.findByEmail(userEmail) ?: throw RuntimeException("User not found")


        val savedVendors = vendorRepository.findByIdIn(user.savedVendors)
        return savedVendors.map { vendorMapper.fromEntity(it) }
    }
}
