package com.ocr.event.module.storage

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

// ponytail: 실제 저장 없이 경로만 만들어 반환, 실제 스토리지 붙일 때 교체
@Component
class FakeStorageModule : StorageModule {

    override fun upload(file: MultipartFile): String =
        "fake/${UUID.randomUUID()}/${file.originalFilename ?: "unknown"}"

}
