package com.example.laundary_backend.auth

import com.example.laundary_backend.auth.refreshToken.RefreshTokenRepository
import com.example.laundary_backend.security.UserAuthentication
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val requestURI = request.requestURI
        val authHeader = request.getHeader("Authorization")

        // Skip authentication check for public endpoints
        if (requestURI.startsWith("/auth/register") ||
            requestURI.startsWith("/auth/login") ||
            requestURI.startsWith("/auth/refresh")
            ) {
            chain.doFilter(request, response)
            return
        }

        // Validate Authorization header
        if (authHeader.isNullOrEmpty() || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header")
            return
        }

        val token = authHeader.substring(7) // Extract token from header


        try {
            if (!jwtUtil.validateToken(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
                return
            }

            val email = jwtUtil.extractEmail(token) ?: run {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: missing email")
                return
            }

            val role = jwtUtil.extractRole(token) ?: run {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: missing role")
                return
            }

//            println("🛡️ JwtFilter: URI = $requestURI")
//            println("🛡️ JwtFilter: Authorization = $authHeader")
//            println("🛡️ JwtFilter: Extracted token = $token")
//            println("🛡️ JwtFilter: Extracted email = $email")
//            println("🛡️ JwtFilter: Extracted role = $role")
//            println("🛡️ JwtFilter: SecurityContext before = ${SecurityContextHolder.getContext().authentication}")

            // Prevent access if the user has logged out
            if (!refreshTokenRepository.existsByUserEmail(email)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User logged out. Please login again.")
                return
            }

            // Authenticate the user if not already authenticated
            if (SecurityContextHolder.getContext().authentication == null) {
                val authentication = UserAuthentication(email, role)

                val securityContext = SecurityContextHolder.createEmptyContext()
                securityContext.authentication = authentication

                authentication.isAuthenticated = true // Ensure authentication is marked as valid
                SecurityContextHolder.setContext(securityContext)
            }

        } catch (e: Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error: ${e.message}")
            println("🛡️ JwtFilter: Authentication error: ${e.message}")
            return
        }

        chain.doFilter(request, response)
    }
}
