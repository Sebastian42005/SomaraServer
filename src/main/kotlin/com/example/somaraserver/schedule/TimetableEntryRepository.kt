package com.example.somaraserver.schedule

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimetableEntryRepository : JpaRepository<TimetableEntry, Long> {
    fun existsByTeacher_Id(teacherId: Long): Boolean

    @Query("select t.name as name, t.color as color from TimetableEntry t")
    fun findAllEntryColors(): List<TimetableEntryColorProjection>
}

interface TimetableEntryColorProjection {
    val name: String
    val color: String
}
