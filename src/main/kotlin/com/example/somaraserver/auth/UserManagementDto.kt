package com.example.somaraserver.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserUpdateRequestDto(
    @field:NotBlank(message = "username darf nicht leer sein")
    var username: String = "",

    @field:NotNull(message = "role darf nicht null sein")
    var role: UserRole? = null
)

data class UserResponseDto(
    val id: Long,
    val username: String,
    val role: UserRole,
    val hasProfileImage: Boolean
)

fun AppUser.toUserResponseDto(): UserResponseDto {
    val persistedId = requireNotNull(id) { "AppUser id darf nicht null sein" }
    return UserResponseDto(
        id = persistedId,
        username = username,
        role = role,
        hasProfileImage = profileImageContent != null
    )
}
