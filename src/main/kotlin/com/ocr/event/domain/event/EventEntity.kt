package com.ocr.event.domain.event

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "tb_event")
class EventEntity(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val type: String,

    @Column(columnDefinition = "TEXT")
    val payload: String,

    /* worker 가 선점한 시각, null 이면 대기 */
    @Column(nullable = true)
    var processingDate: LocalDateTime? = null,

    @Column(nullable = true)
    var publishedDate: LocalDateTime? = null,

    val registrationDate: LocalDateTime

) {

    /* 선점 처리 */
    fun process(processingDate: LocalDateTime) {
        this.processingDate = processingDate
    }

    fun publish(publishedDate: LocalDateTime) {
        this.publishedDate = publishedDate
    }

    /* 선점 해제 */
    fun release() {
        this.processingDate = null
    }

}
