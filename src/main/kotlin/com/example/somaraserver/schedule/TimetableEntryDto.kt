package com.example.somaraserver.schedule

import com.example.somaraserver.teacher.TeacherResponseDto
import com.example.somaraserver.teacher.toTeacherResponseDto
import com.example.somaraserver.yogaclass.YogaClassResponseDto
import com.example.somaraserver.yogaclass.toYogaClassResponseDto
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class TimetableEntryRequestDto(
    @field:NotNull(message = "start darf nicht null sein")
    val start: Instant?,

    @field:NotNull(message = "end darf nicht null sein")
    val end: Instant?,

    val level: Level,

    @field:NotNull(message = "teacherId darf nicht null sein")
    val teacherId: Long?,

    @field:NotNull(message = "yogaClassId darf nicht null sein")
    val yogaClassId: Long?
)

data class TimetableEntryResponseDto(
    val id: Long,
    val start: Instant,
    val end: Instant,
    val level: Level,
    val teacher: TeacherResponseDto,
    val yogaClass: YogaClassResponseDto
)

fun TimetableEntry.toTimetableEntryResponseDto(): TimetableEntryResponseDto {
    val persistedId = requireNotNull(id) { "TimetableEntry id darf nicht null sein" }
    val teacherEntity = requireNotNull(teacher) { "Teacher darf nicht null sein" }
    val yogaClassEntity = requireNotNull(yogaClass) { "YogaClass darf nicht null sein" }

    return TimetableEntryResponseDto(
        id = persistedId,
        start = start,
        end = end,
        level = level,
        teacher = teacherEntity.toTeacherResponseDto(),
        yogaClass = yogaClassEntity.toYogaClassResponseDto()
    )
}
