package com.example.laundary_backend.security

import com.example.laundary_backend.auth.JwtFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtFilter: JwtFilter,
    private val customUserDetailsService: CustomUserDetailsService,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { } // Allow frontend calls
            .authorizeHttpRequests {
                it.requestMatchers("/auth/register", "/auth/login", "/auth/refresh").permitAll() // Public auth routes
                    .requestMatchers("/auth/logout").authenticated() // Logout requires authentication
                    .requestMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN
                    .requestMatchers("/vendors/save/*").hasAnyRole("USER", "VENDOR" , "DEVELOPER" , "ADMIN")
                    .requestMatchers("/vendors/register").hasRole("VENDOR") // Only VENDORS register
                    .requestMatchers("/vendors/**").authenticated() // Protect all vendor routes
                    .requestMatchers("/requests/create" , "/requests/user").hasAnyRole("USER" , "ADMIN")
                    .requestMatchers("/requests/{id}").hasAnyRole("USER" , "ADMIN" ) // USERS delete their requests
                    .requestMatchers("/requests/{id}/accept", "/requests/{id}/reject", "/requests/{id}/pickup").hasRole("VENDOR") // VENDORS manage requests
                    .requestMatchers("/requests/pending/{vendorId}").hasRole("VENDOR") // VENDORS check pending requests
                    .requestMatchers("/developer/**").hasRole("DEVELOPER") // Only Developers
                    .requestMatchers("/users/me").authenticated() // Allow any authenticated user
                    .requestMatchers("/users").hasRole("ADMIN") // Only ADMIN can access other user-related endpoints
                    .anyRequest().authenticated() // Protect all other routes
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customUserDetailsService) // Use our custom service
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

}
