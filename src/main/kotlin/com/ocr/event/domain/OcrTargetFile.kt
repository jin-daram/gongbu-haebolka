package com.ocr.event.domain

import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
class OcrTargetFile(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val name: String,

    val path: String,

    val extension: String

) {}