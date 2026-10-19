package com.ocr.event.service

import com.ocr.event.config.OCR_GROUP
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component

@Component
class OcrReceiver (
    private val redis: StringRedisTemplate
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun onMessage(consumerName: String, record: MapRecord<String, String, String>) {
        val result = record.value["result"]
        log.info("[${consumerName}]OCR 이벤트 수신 recordId: ${record.id}, result: $result")
        redis.opsForStream<String, String>().acknowledge(OCR_GROUP, record)
    }
}