package com.example.somaraserver.auth

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AppUserRepository : JpaRepository<AppUser, Long> {
    fun findByUsername(username: String): Optional<AppUser>

    fun existsByUsername(username: String): Boolean

    fun findAllByOrderByIdAsc(): List<AppUser>

    fun countByRole(role: UserRole): Long
}
