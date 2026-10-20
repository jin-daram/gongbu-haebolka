package com.ocr.event.repository

import com.ocr.event.domain.internalfile.InternalFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InternalFileRepository : JpaRepository<InternalFile, Long> {}