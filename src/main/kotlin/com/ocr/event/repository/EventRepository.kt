package com.ocr.event.repository

import com.ocr.event.domain.event.EventEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface EventRepository : JpaRepository<EventEntity, Long> {

    /* 미처리 + (미선점 or 선점 후 staleBefore 지남) 이벤트, FOR UPDATE SKIP LOCKED 로 다른 worker 가 잡은 행은 건너뛴다 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query("""
        select e from EventEntity e
        where e.publishedDate is null
          and (e.processingDate is null or e.processingDate < :staleBefore)
        order by e.id
    """)
    fun findProcessable(staleBefore: LocalDateTime, limit: Limit): List<EventEntity>

}
