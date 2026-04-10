package com.example.somaraserver.teacher

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/teachers")
class TeacherController(
    private val teacherService: TeacherService
) {
    @GetMapping
    fun getAll(): List<TeacherResponseDto> = teacherService.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TeacherResponseDto = teacherService.getById(id)

    @GetMapping("/{id}/profile-image")
    fun getProfileImage(@PathVariable id: Long): ResponseEntity<ByteArray> =
        teacherService.getProfileImageById(id)

    @PutMapping("/{id}/profile-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfileImage(
        @PathVariable id: Long,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Unit> {
        teacherService.uploadProfileImage(id, file)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun create(
        @RequestParam name: String,
        @RequestPart("description") description: String,
        @RequestParam profileImage: MultipartFile
    ): ResponseEntity<TeacherResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(teacherService.create(name, description, profileImage))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestPart("description") description: String,
        @RequestParam profileImage: MultipartFile
    ): TeacherResponseDto = teacherService.update(id, name, description, profileImage)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        teacherService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
