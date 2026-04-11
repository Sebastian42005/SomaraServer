package com.example.somaraserver.yogaclass

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "yoga_classes")
class YogaClass(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String = "",

    @Column(nullable = false)
    var color: String = "",

    @Column(name = "image_content_type")
    var imageContentType: String? = null,

    @Column(name = "image_content")
    var imageContent: ByteArray? = null
)
