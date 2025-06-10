package com.example.laundary_backend.controller


import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    //  Admin can view all users
    @GetMapping("/getall")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): List<UserDTO?>? {
        println("🔍 Fetching all users...")
        return userService.allUsers
    }

    //  Admins can fetch user
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserById(@PathVariable id: String): UserDTO? {
        return userService.getUserById(id)
    }

    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserDTO> {
        val authentication = SecurityContextHolder.getContext().authentication

        println("Auth Object: $authentication")  // Debugging
        println("Authenticated Name (Email): ${authentication?.name}") // Debugging

        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val email = authentication.name
        val user = userService.getUserByEmail(email)

        return ResponseEntity.ok(user)
    }

}

