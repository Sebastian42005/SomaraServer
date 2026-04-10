package com.example.somaraserver.schedule

import com.example.somaraserver.teacher.Teacher
import com.example.somaraserver.yogaclass.YogaClass
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "schedule_entries")
class TimetableEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "start_time", nullable = false)
    var start: Instant = Instant.EPOCH,

    @Column(name = "end_time", nullable = false)
    var end: Instant = Instant.EPOCH,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var level: Level = Level.BEGINNER,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    var teacher: Teacher? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "yoga_class_id", nullable = false)
    var yogaClass: YogaClass? = null
)
