package com.ocr.event.api.upload

import com.ocr.event.api.upload.request.UploadRequest
import com.ocr.event.service.ocr.UploadService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/upload")
class UploadController(
    private val uploadService: UploadService
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass);

    @PostMapping
    fun upload(@RequestBody request: UploadRequest) {
        log.info("request: {}", request)
        uploadService.upload(request)
    }

}