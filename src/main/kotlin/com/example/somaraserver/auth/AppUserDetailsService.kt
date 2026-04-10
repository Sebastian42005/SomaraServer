package com.example.somaraserver.auth

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AppUserDetailsService(
    private val appUserRepository: AppUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        AppUserPrincipal(appUserRepository.findByUsername(username).orElseThrow {
            UsernameNotFoundException("User mit username=$username wurde nicht gefunden")
        })
}
