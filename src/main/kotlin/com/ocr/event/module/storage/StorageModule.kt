package com.ocr.event.module.storage

import org.springframework.web.multipart.MultipartFile

interface StorageModule {

    // 저장된 파일 경로를 반환
    fun upload(file: MultipartFile): String

}
