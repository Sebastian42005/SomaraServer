package com.example.somaraserver.schedule

import com.example.somaraserver.teacher.TeacherResponseDto
import com.example.somaraserver.teacher.toTeacherResponseDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class TimetableEntryRequestDto(
    @field:NotBlank(message = "name darf nicht leer sein")
    val name: String,

    @field:NotNull(message = "start darf nicht null sein")
    val start: Instant?,

    @field:NotNull(message = "end darf nicht null sein")
    val end: Instant?,

    @field:NotBlank(message = "color darf nicht leer sein")
    val color: String,

    val level: Level,

    @field:NotNull(message = "teacherId darf nicht null sein")
    val teacherId: Long?
)

data class TimetableEntryResponseDto(
    val id: Long,
    val name: String,
    val start: Instant,
    val end: Instant,
    val color: String,
    val level: Level,
    val teacher: TeacherResponseDto
)

data class TimetableEntryColorDto(
    val name: String,
    val colorHex: String
)

fun TimetableEntry.toTimetableEntryResponseDto(): TimetableEntryResponseDto {
    val persistedId = requireNotNull(id) { "TimetableEntry id darf nicht null sein" }
    val teacherEntity = requireNotNull(teacher) { "Teacher darf nicht null sein" }

    return TimetableEntryResponseDto(
        id = persistedId,
        name = name,
        start = start,
        end = end,
        color = color,
        level = level,
        teacher = teacherEntity.toTeacherResponseDto()
    )
}
