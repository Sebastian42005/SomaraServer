package com.example.somaraserver.schedule

import com.example.somaraserver.teacher.TeacherService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class TimetableEntryService(
    private val timetableEntryRepository: TimetableEntryRepository,
    private val teacherService: TeacherService
) {
    fun getAll(): List<TimetableEntryResponseDto> =
        timetableEntryRepository.findAll().map(TimetableEntry::toTimetableEntryResponseDto)

    fun getById(id: Long): TimetableEntryResponseDto =
        findEntityById(id).toTimetableEntryResponseDto()

    fun create(request: TimetableEntryRequestDto): TimetableEntryResponseDto {
        val start = requireNotNull(request.start)
        val end = requireNotNull(request.end)
        validateDates(start, end)

        val teacher = teacherService.findEntityById(requireNotNull(request.teacherId))
        val entry = TimetableEntry(
            name = request.name.trim(),
            start = start,
            end = end,
            color = request.color.trim(),
            level = request.level,
            teacher = teacher
        )

        return timetableEntryRepository.save(entry).toTimetableEntryResponseDto()
    }

    fun update(id: Long, request: TimetableEntryRequestDto): TimetableEntryResponseDto {
        val start = requireNotNull(request.start)
        val end = requireNotNull(request.end)
        validateDates(start, end)

        val teacher = teacherService.findEntityById(requireNotNull(request.teacherId))
        val existing = findEntityById(id)

        existing.name = request.name.trim()
        existing.start = start
        existing.end = end
        existing.color = request.color.trim()
        existing.level = request.level
        existing.teacher = teacher

        return timetableEntryRepository.save(existing).toTimetableEntryResponseDto()
    }

    fun delete(id: Long) {
        val existing = findEntityById(id)
        timetableEntryRepository.delete(existing)
    }

    fun getUsedColors(): List<TimetableEntryColorDto> =
        timetableEntryRepository.findAllEntryColors().map { projection ->
            TimetableEntryColorDto(
                name = projection.name,
                colorHex = projection.color
            )
        }

    private fun findEntityById(id: Long): TimetableEntry =
        timetableEntryRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "TimetableEntry mit id=$id wurde nicht gefunden")
        }

    private fun validateDates(start: Instant, end: Instant) {
        if (!end.isAfter(start)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "end muss nach start liegen"
            )
        }
    }
}
