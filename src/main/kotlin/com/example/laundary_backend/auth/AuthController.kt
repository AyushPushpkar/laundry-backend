package com.example.laundary_backend.auth

import com.example.laundary_backend.dto.UserDTO
import com.example.laundary_backend.security.UserAuthentication
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@RequestBody userDTO: UserDTO): ResponseEntity<Map<String, Any>> {
        val message = authService.register(userDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("status" to "success", "message" to message)
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<Map<String, Any>> {
        val tokens = authService.login(loginRequest.email, loginRequest.password)
        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "message" to "Login successful",
                "data" to tokens
            )
        )
    }

    data class RefreshTokenRequest(val refreshToken: String)

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<Map<String, Any>> {
        val newAccessToken = authService.refreshAccessToken(request.refreshToken)
        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "message" to "Access token refreshed successfully",
                "data" to mapOf("accessToken" to newAccessToken)
            )
        )
    }


    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, Any>> {
        val authentication = SecurityContextHolder.getContext().authentication
        return if (authentication == null || authentication !is UserAuthentication) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                mapOf("status" to "error", "message" to "User is not authenticated!")
            )
        } else {
            authService.logout(authentication.email)
            ResponseEntity.ok(
                mapOf("status" to "success", "message" to "User logged out successfully!")
            )
        }
    }



}


