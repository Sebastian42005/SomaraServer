package com.example.somaraserver.teacher

import com.example.somaraserver.schedule.TimetableEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

private const val MAX_PROFILE_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L

@Service
class TeacherService(
    private val teacherRepository: TeacherRepository,
    private val timetableEntryRepository: TimetableEntryRepository
) {
    fun getAll(): List<TeacherResponseDto> =
        teacherRepository.findAll().map(Teacher::toTeacherResponseDto)

    fun getById(id: Long): TeacherResponseDto =
        findEntityById(id).toTeacherResponseDto()

    fun getProfileImageById(id: Long): ResponseEntity<ByteArray> {
        val teacher = findEntityById(id)
        val imageContent = teacher.profileImageContent
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher mit id=$id hat kein Profilbild")

        val mediaType = teacher.profileImageContentType
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

    fun uploadProfileImage(id: Long, file: MultipartFile) {
        if (file.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild darf nicht leer sein")
        }
        if (file.size > MAX_PROFILE_IMAGE_SIZE_BYTES) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild darf maximal 5 MB gross sein")
        }

        val contentType = file.contentType?.trim()?.takeIf { it.isNotEmpty() }
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Content-Type fuer Profilbild fehlt")
        val mediaType = runCatching { MediaType.parseMediaType(contentType) }.getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungueltiger Content-Type fuer Profilbild")
        }
        if (!"image".equals(mediaType.type, ignoreCase = true)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild muss ein Bild-Content-Type sein")
        }

        val teacher = findEntityById(id)
        teacher.profileImageContentType = mediaType.toString()
        teacher.profileImageContent = file.bytes
        teacherRepository.save(teacher)
    }

    fun create(name: String, description: String, profileImage: MultipartFile): TeacherResponseDto {
        val teacher = Teacher(
            name = name.trim(),
            description = description.trim(),
            profileImageContent = profileImage.bytes,
            profileImageContentType = profileImage.contentType
        )
        return teacherRepository.save(teacher).toTeacherResponseDto()
    }

    fun update(id: Long, name: String, description: String, profileImage: MultipartFile): TeacherResponseDto {
        val teacher = findEntityById(id)
        teacher.name = name.trim()
        teacher.description = description.trim()
        teacher.profileImageContent = profileImage.bytes
        teacher.profileImageContentType = profileImage.contentType
        return teacherRepository.save(teacher).toTeacherResponseDto()
    }

    fun delete(id: Long) {
        val teacher = findEntityById(id)
        if (timetableEntryRepository.existsByTeacher_Id(id)) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Teacher mit id=$id kann nicht geloescht werden, da noch TimetableEntries zugeordnet sind"
            )
        }
        teacherRepository.delete(teacher)
    }

    fun findEntityById(id: Long): Teacher =
        teacherRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher mit id=$id wurde nicht gefunden")
        }
}

private fun String?.normalizeOptionalText(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
