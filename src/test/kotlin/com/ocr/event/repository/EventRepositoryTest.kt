package com.ocr.event.repository

import com.ocr.event.domain.event.EventEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.domain.Limit
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class EventRepositoryTest(
    @Autowired private val eventRepository: EventRepository,
    @Autowired transactionManager: PlatformTransactionManager
) {

    private val tx = TransactionTemplate(transactionManager)

    private fun find(now: LocalDateTime) = eventRepository.findProcessable(now.minusMinutes(5), Limit.of(100))

    @Test
    fun `다른 worker 가 잡은 이벤트는 건너뛴다`() {
        eventRepository.deleteAll()
        repeat(2) { eventRepository.save(EventEntity(type = "T", payload = "{}", registrationDate = LocalDateTime.now())) }
        val now = LocalDateTime.now()

        tx.executeWithoutResult {
            val first = find(now)

            // 락을 잡은 상태에서 다른 worker 가 조회 → 기다리지 않고 0건이어야 한다
            val second = CompletableFuture.supplyAsync { tx.execute { find(now) }!! }.get(5, TimeUnit.SECONDS)

            assertEquals(2, first.size)
            assertEquals(0, second.size)
        }
    }

    @Test
    fun `선점된 이벤트는 만료 전엔 건너뛰고 만료 후엔 다시 가져온다`() {
        eventRepository.deleteAll()
        val now = LocalDateTime.now()
        val fresh = eventRepository.save(EventEntity(type = "T", payload = "{}", processingDate = now, registrationDate = now))
        val stale = eventRepository.save(EventEntity(type = "T", payload = "{}", processingDate = now.minusMinutes(10), registrationDate = now))
        eventRepository.save(EventEntity(type = "T", payload = "{}", publishedDate = now, registrationDate = now))

        val found = tx.execute { find(now) }!!.map { it.id }

        assertEquals(listOf(stale.id), found)
        assert(fresh.id !in found)
    }

}
