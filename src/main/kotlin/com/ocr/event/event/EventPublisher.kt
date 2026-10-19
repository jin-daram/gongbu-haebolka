package com.ocr.event.event

import com.ocr.event.config.OCR_STREAM
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class EventPublisher(
    private val redis: StringRedisTemplate,
    private val jsonMapper: JsonMapper
) {

    fun publish(event: Event) {
        redis.opsForStream<String, String>().add(
            OCR_STREAM,
            mapOf("type" to event.javaClass.simpleName, "payload" to jsonMapper.writeValueAsString(event))
        )
    }

}