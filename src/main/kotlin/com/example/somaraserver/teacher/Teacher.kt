package com.example.somaraserver.teacher

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "teachers")
class Teacher(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(name = "profile_image_content_type")
    var profileImageContentType: String? = null,

    @Column(name = "profile_image_content")
    var profileImageContent: ByteArray? = null
)
