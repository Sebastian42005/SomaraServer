package com.example.somaraserver.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Level(@get:JsonValue val value: String) {
    BEGINNER("beginner"),
    ADVANCED("advanced"),
    ALL_LEVELS("all levels");

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): Level {
            val normalized = value.trim()
            return values().firstOrNull { it.value.equals(normalized, ignoreCase = true) }
                ?: throw IllegalArgumentException("Ungültiges level '$value'. Erlaubt: beginner, advanced, all levels")
        }
    }
}
