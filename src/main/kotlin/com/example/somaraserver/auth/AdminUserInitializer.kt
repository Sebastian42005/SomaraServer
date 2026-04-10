package com.example.somaraserver.auth

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminUserInitializer(
    private val appUserRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.security.admin.username}")
    private val adminUsername: String,
    @Value("\${app.security.admin.password}")
    private val adminPassword: String
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(AdminUserInitializer::class.java)

    override fun run(vararg args: String) {
        val username = adminUsername.trim()
        if (username.isBlank() || adminPassword.isBlank()) {
            log.warn("Admin User wurde nicht erstellt, da username/password leer sind")
            return
        }

        if (appUserRepository.existsByUsername(username)) {
            return
        }

        val adminUser = AppUser(
            username = username,
            password = requireNotNull(passwordEncoder.encode(adminPassword)) {
                "Admin-Passwort konnte nicht kodiert werden"
            },
            role = UserRole.ADMIN
        )
        appUserRepository.save(adminUser)
        log.info("Admin User '{}' wurde erstellt", username)
    }
}
