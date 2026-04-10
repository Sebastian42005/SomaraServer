package com.example.somaraserver.yogaclass

import jakarta.validation.constraints.NotBlank

data class YogaClassRequestDto(
    @field:NotBlank(message = "name darf nicht leer sein")
    val name: String,

    @field:NotBlank(message = "description darf nicht leer sein")
    val description: String,

    @field:NotBlank(message = "color darf nicht leer sein")
    val color: String
)

data class YogaClassResponseDto(
    val id: Long,
    val name: String,
    val description: String,
    val color: String,
    val hasImage: Boolean
)

data class YogaClassColorDto(
    val name: String,
    val colorHex: String
)

fun YogaClass.toYogaClassResponseDto(): YogaClassResponseDto {
    val persistedId = requireNotNull(id) { "YogaClass id darf nicht null sein" }
    return YogaClassResponseDto(
        id = persistedId,
        name = name,
        description = description,
        color = color,
        hasImage = imageContent != null
    )
}
