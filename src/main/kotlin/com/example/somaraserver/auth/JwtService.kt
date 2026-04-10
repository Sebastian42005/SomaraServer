package com.example.somaraserver.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.security.jwt.secret}")
    private val jwtSecret: String,
    @Value("\${app.security.jwt.expiration-ms:86400000}")
    private val jwtExpirationMs: Long
) {
    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        extractUsername(token) == userDetails.username && !isTokenExpired(token)

    fun generateToken(userDetails: UserDetails): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(Date(now))
            .expiration(Date(now + jwtExpirationMs))
            .signWith(signingKey())
            .compact()
    }

    private fun isTokenExpired(token: String): Boolean =
        extractClaim(token, Claims::getExpiration).before(Date())

    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .payload

    private fun signingKey(): SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
}

