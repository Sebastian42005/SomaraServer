package com.example.somaraserver.auth

import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val appUserRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {
    fun register(request: RegisterRequestDto): AuthResponseDto {
        val username = request.username.trim()
        if (appUserRepository.existsByUsername(username)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username ist bereits vergeben")
        }

        val user = AppUser(
            username = username,
            password = requireNotNull(passwordEncoder.encode(request.password)) {
                "Passwort konnte nicht kodiert werden"
            },
            role = UserRole.USER
        )

        val savedUser = appUserRepository.save(user)
        val token = jwtService.generateToken(AppUserPrincipal(savedUser))
        return AuthResponseDto(
            token = token,
            username = savedUser.username,
            role = savedUser.role
        )
    }

    fun login(request: LoginRequestDto): AuthResponseDto {
        val username = request.username.trim()
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, request.password)
        )

        val user = appUserRepository.findByUsername(username).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungueltige Login-Daten")
        }
        val token = jwtService.generateToken(AppUserPrincipal(user))
        return AuthResponseDto(
            token = token,
            username = user.username,
            role = user.role
        )
    }
}
