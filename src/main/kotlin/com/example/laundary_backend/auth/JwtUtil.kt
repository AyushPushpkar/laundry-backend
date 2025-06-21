package com.example.laundary_backend.auth


import com.example.laundary_backend.entity.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import org.springframework.stereotype.Component
import java.util.Date
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import java.time.Clock
import java.security.Key


@Component
class JwtUtil (
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long ,
    private val clock: Clock = Clock.systemUTC()
){
//    val secret = "YjNhNmEyMTQtNzU1Ni00M2MzLWI2ZmQtYzIxMjQ5ZDVmZGYy" ;

    private lateinit var secretKey: Key

    @PostConstruct
    fun init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

        require(secretKey.encoded.size >= 32) {
            "JWT secret must be at least 256 bits (32 bytes) for HS256"
        }
    }

   // private val secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))

    fun generateAccessToken(email: String, role: UserRole): String {
        return generateToken(email, role, accessTokenExpiration)
    }

    fun generateRefreshToken(email: String): String {
        return generateToken(email, null, refreshTokenExpiration)
    }

    private fun generateToken(email: String, role: UserRole?, expiration: Long): String {
        val now = Date.from(clock.instant())
        val expiryDate = Date(now.time + expiration)

        val builder = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)

        role?.let {
            builder.claim("role", it.name)
            builder.claim("type", "access")
        } ?: builder.claim("type", "refresh")

        return builder.compact()
    }

    fun extractEmail(token: String): String? {
        return getClaims(token)?.subject
    }

    fun extractRole(token: String): UserRole? {
        return getClaims(token)?.get("role", String::class.java)?.let {
            runCatching { UserRole.valueOf(it) }.getOrNull()
        }
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims?.expiration?.after(Date()) ?: false // Ensure token is not expired
        } catch (e: JwtException) {
            false // Invalid or expired token
        }
    }

    private fun getClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            if (isDev()) e.printStackTrace()
            null // Return null if parsing fails
        }
    }
}

private fun isDev(): Boolean = System.getenv("SPRING_PROFILES_ACTIVE") == "dev"