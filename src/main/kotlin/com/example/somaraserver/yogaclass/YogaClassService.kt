package com.example.somaraserver.yogaclass

import com.example.somaraserver.schedule.TimetableEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

private const val MAX_CLASS_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L

@Service
class YogaClassService(
    private val yogaClassRepository: YogaClassRepository,
    private val timetableEntryRepository: TimetableEntryRepository
) {
    fun getAll(): List<YogaClassResponseDto> =
        yogaClassRepository.findAll().map(YogaClass::toYogaClassResponseDto)

    fun getById(id: Long): YogaClassResponseDto =
        findEntityById(id).toYogaClassResponseDto()

    fun getColors(): List<YogaClassColorDto> =
        yogaClassRepository.findAll()
            .distinctBy { yogaClass -> yogaClass.color.trim().lowercase() }
            .map { yogaClass ->
                YogaClassColorDto(
                    name = yogaClass.name,
                    colorHex = yogaClass.color.trim()
                )
            }

    fun create(request: YogaClassRequestDto): YogaClassResponseDto {
        val yogaClass = YogaClass(
            name = request.name.trim(),
            description = request.description.trim(),
            color = request.color.trim()
        )
        return yogaClassRepository.save(yogaClass).toYogaClassResponseDto()
    }

    fun update(id: Long, request: YogaClassRequestDto): YogaClassResponseDto {
        val yogaClass = findEntityById(id)
        yogaClass.name = request.name.trim()
        yogaClass.description = request.description.trim()
        yogaClass.color = request.color.trim()
        return yogaClassRepository.save(yogaClass).toYogaClassResponseDto()
    }

    fun getImageById(id: Long): ResponseEntity<ByteArray> {
        val yogaClass = findEntityById(id)
        val imageContent = yogaClass.imageContent
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "YogaClass mit id=$id hat kein Bild")

        val mediaType = yogaClass.imageContentType
            ?.let { contentType ->
                runCatching { MediaType.parseMediaType(contentType) }.getOrElse {
                    MediaType.APPLICATION_OCTET_STREAM
                }
            }
            ?: MediaType.APPLICATION_OCTET_STREAM

        return ResponseEntity.status(HttpStatus.OK)
            .contentType(mediaType)
            .body(imageContent)
    }

    fun uploadImage(id: Long, file: MultipartFile) {
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bild darf nicht leer sein")
        }
        if (file.size > MAX_CLASS_IMAGE_SIZE_BYTES) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bild darf maximal 5 MB gross sein")
        }

        val contentType = file.contentType?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Content-Type fuer Bild fehlt")
        val mediaType = runCatching { MediaType.parseMediaType(contentType) }.getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungueltiger Content-Type fuer Bild")
        }
        if (!"image".equals(mediaType.type, ignoreCase = true)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Bild muss ein Bild-Content-Type sein")
        }

        val yogaClass = findEntityById(id)
        yogaClass.imageContentType = mediaType.toString()
        yogaClass.imageContent = file.bytes
        yogaClassRepository.save(yogaClass)
    }

    fun delete(id: Long) {
        val yogaClass = findEntityById(id)
        if (timetableEntryRepository.existsByYogaClass_Id(id)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "YogaClass mit id=$id kann nicht geloescht werden, da noch TimetableEntries zugeordnet sind"
            )
        }
        yogaClassRepository.delete(yogaClass)
    }

    fun findEntityById(id: Long): YogaClass =
        yogaClassRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "YogaClass mit id=$id wurde nicht gefunden")
        }
}
