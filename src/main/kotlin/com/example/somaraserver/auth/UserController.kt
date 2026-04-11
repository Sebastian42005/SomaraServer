package com.example.somaraserver.auth

import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping
    fun getAllUsers(): List<UserResponseDto> = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserResponseDto =
        userService.getUserById(id)

    @GetMapping("/me")
    fun getCurrentUser(): UserResponseDto = userService.getCurrentUser()

    @GetMapping("/{id}/profile-image")
    fun getUserProfileImage(@PathVariable id: Long): ResponseEntity<ByteArray> =
        userService.getUserProfileImageById(id)

    @GetMapping("/me/profile-image")
    fun getCurrentUserProfileImage(): ResponseEntity<ByteArray> =
        userService.getCurrentUserProfileImage()

    @PutMapping("/{id}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateUser(
        @PathVariable id: Long,
        @Valid @ModelAttribute request: UserUpdateRequestDto,
        @RequestPart("profileImage", required = false) profileImage: MultipartFile?
    ): UserResponseDto = userService.updateUser(id, request, profileImage)
}
