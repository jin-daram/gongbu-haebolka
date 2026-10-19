package com.ocr.event.repository

import com.ocr.event.domain.OcrTargetFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OcrTargetFileRepository : JpaRepository<OcrTargetFile, Long> {
}