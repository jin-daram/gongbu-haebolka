package com.ocr.event.config

import com.ocr.event.service.OcrReceiver
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import java.time.Duration


const val OCR_STREAM = "ocr-stream"
const val OCR_GROUP = "ocr-group"

@Configuration
class RedisStreamsConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean(destroyMethod = "stop")
    fun ocrListenerContainer(
        factory: RedisConnectionFactory,
        redis: StringRedisTemplate,
        ocrReceiver: OcrReceiver
    ): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        createGroupIfAbsent(redis)

        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
            .pollTimeout(Duration.ofSeconds(1))
            .batchSize(1) // 한 consumer가 메시지를 몰아가지 않도록 1개씩 읽는다
            .build();

        val container = StreamMessageListenerContainer.create(factory, options)

        repeat(3) { i ->
            val name = "consumer-$i"
            container.register(
                StreamMessageListenerContainer.StreamReadRequest
                    .builder(StreamOffset.create(OCR_STREAM, ReadOffset.lastConsumed()))
                    .consumer(Consumer.from(OCR_GROUP, name))
                    .autoAcknowledge(false)
                    .cancelOnError { false }
                    .build(),
            ) { record -> ocrReceiver.onMessage(name, record) }
        }

        container.start()
        return container
    }

    private fun createGroupIfAbsent(redis: StringRedisTemplate) {
        try {
            redis.execute { conn ->
                conn.streamCommands().xGroupCreate(OCR_STREAM.toByteArray(), OCR_GROUP, ReadOffset.from("0"), true)
            }
        } catch (e: RedisSystemException) {
            log.debug("consumer group 이미 존재: {}", OCR_GROUP)
        }
    }

}