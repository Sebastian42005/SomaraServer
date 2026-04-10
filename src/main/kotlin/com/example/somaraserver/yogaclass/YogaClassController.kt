package com.example.somaraserver.yogaclass

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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/yoga-classes")
class YogaClassController(
    private val yogaClassService: YogaClassService
) {
    @GetMapping
    fun getAll(): List<YogaClassResponseDto> = yogaClassService.getAll()

    @GetMapping("/colors")
    fun getColors(): List<YogaClassColorDto> = yogaClassService.getColors()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): YogaClassResponseDto = yogaClassService.getById(id)

    @GetMapping("/{id}/image")
    fun getImage(@PathVariable id: Long): ResponseEntity<ByteArray> =
        yogaClassService.getImageById(id)

    @PutMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @PathVariable id: Long,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Unit> {
        yogaClassService.uploadImage(id, file)
        return ResponseEntity.noContent().build()
    }

    @PostMapping
    fun create(@Valid @RequestBody request: YogaClassRequestDto): ResponseEntity<YogaClassResponseDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(yogaClassService.create(request))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: YogaClassRequestDto
    ): YogaClassResponseDto = yogaClassService.update(id, request)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
        yogaClassService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
