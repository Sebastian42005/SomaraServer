package com.example.somaraserver.config

import com.example.somaraserver.auth.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    @Value("\${app.security.cors.allowed-origins:http://localhost:4200}")
    private val corsAllowedOrigins: String
) {
    companion object {
        private const val AUTH_ENDPOINT = "/api/auth/**"
        private const val TEACHERS_ENDPOINT = "/api/teachers/**"
        private const val TIMETABLE_ENTRIES_ENDPOINT = "/api/timetable-entries/**"
        private val WRITE_METHODS = arrayOf(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .cors { }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                auth.requestMatchers(AUTH_ENDPOINT).permitAll()

                auth.requestMatchers(HttpMethod.GET, TEACHERS_ENDPOINT).permitAll()
                WRITE_METHODS.forEach {
                    auth.requestMatchers(it, TEACHERS_ENDPOINT).hasRole("ADMIN")
                }

                auth.requestMatchers(HttpMethod.GET, TIMETABLE_ENTRIES_ENDPOINT).permitAll()
                WRITE_METHODS.forEach {
                    auth.requestMatchers(it, TIMETABLE_ENTRIES_ENDPOINT).hasAnyRole("TEACHER", "ADMIN")
                }

                auth.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val parsedOrigins = corsAllowedOrigins
            .splitToSequence(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toList()

        val corsConfiguration = CorsConfiguration().apply {
            allowedOrigins = parsedOrigins
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", corsConfiguration)
        }
    }
}
