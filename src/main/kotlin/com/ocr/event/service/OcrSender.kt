package com.ocr.event.service

import com.ocr.event.config.OCR_STREAM
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class OcrSender(
    private val redis: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun send(data: String) {
        val recordId = redis.opsForStream<String, String>().add(OCR_STREAM, mapOf("result" to data))

        log.info("OCR 이벤트 발행 recordId: $recordId")
    }

}