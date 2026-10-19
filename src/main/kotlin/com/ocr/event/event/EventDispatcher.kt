package com.ocr.event.event

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class EventDispatcher(
    handlers: List<EventHandler<*>>,
    private val redis: StringRedisTemplate,
    private val jsonMapper: JsonMapper
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val handlers = handlers.associateBy { it.eventType.simpleName }

    fun dispatch(consumer: String, record: MapRecord<String, String, String>) {
        val type = record.value["type"]
        val handler = handlers[type] as EventHandler<Event>? ?: error("핸들러 없음 type: $type")

        log.info("[$consumer] 이벤트 수신 recordId: ${record.id}, type: $type")
        handler.handle(jsonMapper.readValue(record.value["payload"], handler.eventType))

        redis.opsForStream<String, String>().acknowledge(consumer, record)
    }


}