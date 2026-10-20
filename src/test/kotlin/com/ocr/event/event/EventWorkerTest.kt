package com.ocr.event.event

import com.ocr.event.domain.event.EventEntity
import com.ocr.event.repository.EventRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OkEvent(val n: Int) : Event
class FailEvent(val n: Int) : Event

@SpringBootTest
class EventWorkerTest(
    @Autowired private val eventRepository: EventRepository
) {

    @TestConfiguration
    class Handlers {
        @Bean fun ok() = object : EventHandler<OkEvent> {
            override val eventType = OkEvent::class.java
            override fun handle(event: OkEvent) {}
        }
        @Bean fun fail() = object : EventHandler<FailEvent> {
            override val eventType = FailEvent::class.java
            override fun handle(event: FailEvent) = error("boom")
        }
    }

    @Test
    fun `성공한 이벤트는 publishedDate 가 찍히고 실패한 이벤트는 선점이 풀린다`() {
        val now = LocalDateTime.now()
        val oks = (1..10).map { eventRepository.save(EventEntity(type = "OkEvent", payload = """{"n":$it}""", registrationDate = now)) }
        val fail = eventRepository.save(EventEntity(type = "FailEvent", payload = """{"n":1}""", registrationDate = now))

        // 스케줄러(1초 주기)가 처리할 때까지 대기
        val deadline = System.currentTimeMillis() + 10_000
        while (System.currentTimeMillis() < deadline &&
            oks.any { eventRepository.findById(it.id!!).get().publishedDate == null }) Thread.sleep(200)

        oks.forEach { assertNotNull(eventRepository.findById(it.id!!).get().publishedDate) }
        assertNull(eventRepository.findById(fail.id!!).get().publishedDate)
        assertEquals(10, oks.size)
    }

}
