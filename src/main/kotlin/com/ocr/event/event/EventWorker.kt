package com.ocr.event.event

import com.ocr.event.domain.event.EventEntity
import com.ocr.event.repository.EventRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Limit
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import tools.jackson.databind.json.JsonMapper
import java.time.LocalDateTime

/* tb_event 를 폴링해서 선점(processingDate) 후 핸들러를 비동기로 실행한다 */
@Component
class EventWorker(
    handlers: List<EventHandler<*>>,
    private val eventRepository: EventRepository,
    private val jsonMapper: JsonMapper,
    private val taskExecutor: ThreadPoolTaskExecutor,
    transactionManager: PlatformTransactionManager
) {

    companion object {
        // ponytail: 고정 5분, 핸들러가 5분 넘게 걸리면 다른 worker 가 다시 가져가 중복 처리됨 → 핸들러 멱등성 필수
        private const val STALE_MINUTES = 5L
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val handlers = handlers.associateBy { it.eventType.simpleName }

    private val tx = TransactionTemplate(transactionManager)

    @Scheduled(fixedDelay = 1000)
    fun poll() {
        // 남는 스레드만큼만 선점한다. 큐에 쌓아두면 대기 중에 선점이 만료돼 중복 처리될 수 있다
        val free = taskExecutor.corePoolSize - taskExecutor.activeCount - taskExecutor.queueSize
        if (free <= 0) return

        val now = LocalDateTime.now()
        val events = tx.execute {
            eventRepository.findProcessable(now.minusMinutes(STALE_MINUTES), Limit.of(free))
                .onEach { it.process(now) }
        }!!

        events.forEach { event -> taskExecutor.execute { handle(event) } }
    }

    private fun handle(event: EventEntity) {
        try {
            /* Event 처리 할 수 있는 Handler 찾기 */
            val handler = handlers[event.type] as EventHandler<Event>? ?: error("핸들러 없음 type: ${event.type}")

            /* 비즈니스 로직 처리 */
            handler.handle(jsonMapper.readValue(event.payload, handler.eventType))

            /* 이벤트 완료 처리 */
            event.publish(LocalDateTime.now())
        } catch (e: Exception) {
            log.error("이벤트 처리 실패 id: ${event.id}, type: ${event.type}", e)
            event.release()
        }
        eventRepository.save(event)
    }

}
