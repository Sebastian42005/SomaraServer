package com.example.somaraserver.schedule

import org.springframework.data.jpa.repository.JpaRepository

interface TimetableEntryRepository : JpaRepository<TimetableEntry, Long> {
    fun existsByTeacher_Id(teacherId: Long): Boolean
    fun existsByYogaClass_Id(yogaClassId: Long): Boolean
}
