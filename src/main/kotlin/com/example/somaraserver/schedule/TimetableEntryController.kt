package com.example.somaraserver.schedule

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/timetable-entries")
class TimetableEntryController(
    private val timetableEntryService: TimetableEntryService
) {
    @GetMapping
    fun getAll(): List<TimetableEntryResponseDto> = timetableEntryService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TimetableEntryResponseDto =
        timetableEntryService.getById(id)

    @PostMapping
    fun create(@Valid @RequestBody request: TimetableEntryRequestDto): ResponseEntity<TimetableEntryResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(timetableEntryService.create(request))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: TimetableEntryRequestDto
    ): TimetableEntryResponseDto = timetableEntryService.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        timetableEntryService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
