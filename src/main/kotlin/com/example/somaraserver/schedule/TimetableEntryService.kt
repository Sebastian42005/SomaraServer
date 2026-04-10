package com.example.somaraserver.schedule

import com.example.somaraserver.teacher.TeacherService
import com.example.somaraserver.yogaclass.YogaClassService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class TimetableEntryService(
    private val timetableEntryRepository: TimetableEntryRepository,
    private val teacherService: TeacherService,
    private val yogaClassService: YogaClassService
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
        val yogaClass = yogaClassService.findEntityById(requireNotNull(request.yogaClassId))
        val entry = TimetableEntry(
            start = start,
            end = end,
            level = request.level,
            teacher = teacher,
            yogaClass = yogaClass
        )

        return timetableEntryRepository.save(entry).toTimetableEntryResponseDto()
    }

    fun update(id: Long, request: TimetableEntryRequestDto): TimetableEntryResponseDto {
        val start = requireNotNull(request.start)
        val end = requireNotNull(request.end)
        validateDates(start, end)

        val teacher = teacherService.findEntityById(requireNotNull(request.teacherId))
        val yogaClass = yogaClassService.findEntityById(requireNotNull(request.yogaClassId))
        val existing = findEntityById(id)

        existing.start = start
        existing.end = end
        existing.level = request.level
        existing.teacher = teacher
        existing.yogaClass = yogaClass

        return timetableEntryRepository.save(existing).toTimetableEntryResponseDto()
    }

    fun delete(id: Long) {
        val existing = findEntityById(id)
        timetableEntryRepository.delete(existing)
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
