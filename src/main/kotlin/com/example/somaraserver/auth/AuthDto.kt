package com.example.somaraserver.auth

import jakarta.validation.constraints.NotBlank

data class RegisterRequestDto(
    @field:NotBlank(message = "username darf nicht leer sein")
    val username: String,

    @field:NotBlank(message = "password darf nicht leer sein")
    val password: String
)

data class LoginRequestDto(
    @field:NotBlank(message = "username darf nicht leer sein")
    val username: String,

    @field:NotBlank(message = "password darf nicht leer sein")
    val password: String
)

data class AuthResponseDto(
    val token: String,
    val username: String,
    val role: UserRole
)
