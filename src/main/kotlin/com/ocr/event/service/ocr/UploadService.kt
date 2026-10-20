package com.ocr.event.service.ocr

import com.ocr.event.api.upload.request.UploadRequest
import com.ocr.event.module.ocr.OcrModule
import com.ocr.event.module.storage.InMemoryMultipartFile
import com.ocr.event.module.storage.StorageModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadService(
    private val storageModule: StorageModule,
    private val ocrModule: OcrModule,
    private val ocrSender: OcrSender
) {

    val log: Logger = LoggerFactory.getLogger(this.javaClass);

    /* 파일 업로드 */
    @Transactional
    fun upload(request: UploadRequest) {
        val path = storageModule.upload(InMemoryMultipartFile("file", "test.png", "image/png", "dummy".toByteArray()))
        log.info("파일이 S3에 업로드 되었습니다. path: {}", path)

        // OCR 처리
        val result = ocrModule.ocr("dummy")
        ocrSender.send(result)
    }

}