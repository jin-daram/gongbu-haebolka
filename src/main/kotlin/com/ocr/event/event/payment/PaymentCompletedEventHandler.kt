package com.ocr.event.event.payment

import com.ocr.event.event.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PaymentCompletedEventHandler : EventHandler<PaymentCompletedEvent> {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override val eventType = PaymentCompletedEvent::class.java

    override fun handle(event: PaymentCompletedEvent) {
        log.info("결제 완료 이벤트 처리 paymentId: ${event.paymentId}")
    }

}
