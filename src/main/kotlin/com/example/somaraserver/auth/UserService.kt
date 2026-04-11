package com.example.somaraserver.auth

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

private const val MAX_USER_PROFILE_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L

@Service
class UserService(
    private val appUserRepository: AppUserRepository
) {
    fun getAllUsers(): List<UserResponseDto> =
        appUserRepository.findAllByOrderByIdAsc().map(AppUser::toUserResponseDto)

    fun getUserById(id: Long): UserResponseDto =
        findEntityById(id).toUserResponseDto()

    fun getCurrentUser(): UserResponseDto =
        findCurrentUserEntity().toUserResponseDto()

    fun getCurrentUserProfileImage(): ResponseEntity<ByteArray> =
        getUserProfileImageById(requireNotNull(findCurrentUserEntity().id))

    fun getUserProfileImageById(id: Long): ResponseEntity<ByteArray> {
        val user = findEntityById(id)
        val imageContent = user.profileImageContent
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User mit id=$id hat kein Profilbild")

        val mediaType = user.profileImageContentType
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

    fun updateUser(id: Long, request: UserUpdateRequestDto, profileImage: MultipartFile?): UserResponseDto {
        val requestedRole = requireNotNull(request.role)
        val normalizedUsername = request.username.trim()
        val user = findEntityById(id)

        val userWithSameUsername = appUserRepository.findByUsername(normalizedUsername).orElse(null)
        if (userWithSameUsername != null && userWithSameUsername.id != user.id) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Username ist bereits vergeben")
        }

        if (user.role == UserRole.ADMIN && requestedRole != UserRole.ADMIN) {
            val adminCount = appUserRepository.countByRole(UserRole.ADMIN)
            if (adminCount <= 1L) {
                throw ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Die Rolle des letzten Admins kann nicht geaendert werden"
                )
            }
        }

        user.username = normalizedUsername
        user.role = requestedRole

        if (profileImage != null) {
            if (profileImage.isEmpty) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild darf nicht leer sein")
            }
            if (profileImage.size > MAX_USER_PROFILE_IMAGE_SIZE_BYTES) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild darf maximal 5 MB gross sein")
            }

            val contentType = profileImage.contentType?.trim()?.takeIf { it.isNotEmpty() }
                ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Content-Type fuer Profilbild fehlt")
            val mediaType = runCatching { MediaType.parseMediaType(contentType) }.getOrElse {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungueltiger Content-Type fuer Profilbild")
            }
            if (!"image".equals(mediaType.type, ignoreCase = true)) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Profilbild muss ein Bild-Content-Type sein")
            }

            user.profileImageContentType = mediaType.toString()
            user.profileImageContent = profileImage.bytes
        }

        return appUserRepository.save(user).toUserResponseDto()
    }

    private fun findEntityById(id: Long): AppUser =
        appUserRepository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User mit id=$id wurde nicht gefunden")
        }

    private fun findCurrentUserEntity(): AppUser {
        val username = SecurityContextHolder.getContext().authentication?.name?.trim()
        if (username.isNullOrBlank() || username == "anonymousUser") {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Benutzer ist nicht authentifiziert")
        }
        return appUserRepository.findByUsername(username).orElseThrow {
            ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentifizierter User wurde nicht gefunden")
        }
    }
}
