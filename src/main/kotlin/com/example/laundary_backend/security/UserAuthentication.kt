package com.example.laundary_backend.security

import com.example.laundary_backend.entity.UserRole
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class UserAuthentication(val email: String, private val role: UserRole) :
    AbstractAuthenticationToken(listOf(SimpleGrantedAuthority("ROLE_${role.name}"))) {

    init {
        isAuthenticated = true //  Mark authentication as valid
    }

    override fun getCredentials(): Any? = null

    override fun getPrincipal(): Any = email
    override fun getName(): String = email

}

