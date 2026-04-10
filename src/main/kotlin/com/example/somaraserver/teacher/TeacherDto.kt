package com.example.somaraserver.teacher


data class TeacherResponseDto(
    val id: Long,
    val name: String,
    val hasProfileImage: Boolean
)

fun Teacher.toTeacherResponseDto(): TeacherResponseDto {
    val persistedId = requireNotNull(id) { "Teacher id darf nicht null sein" }
    return TeacherResponseDto(
        id = persistedId,
        name = name,
        hasProfileImage = profileImageContent != null
    )
}
