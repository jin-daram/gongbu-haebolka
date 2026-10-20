package com.ocr.event.service.payment

import com.ocr.event.domain.event.EventEntity
import com.ocr.event.domain.payment.Payment
import com.ocr.event.domain.payment.PaymentStatus
import com.ocr.event.domain.payment.PaymentType
import com.ocr.event.event.payment.PaymentCompletedEvent
import com.ocr.event.repository.EventRepository
import com.ocr.event.repository.PaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val eventRepository: EventRepository,
    private val jsonMapper: JsonMapper
) {

    fun requestPayment(amount: BigDecimal, type: PaymentType) {
        val newPayment = Payment(
            id = null,
            status = PaymentStatus.PENDING,
            type = type,
            amount = amount,
            paidDate = null,
            LocalDateTime.now()
        );

        paymentRepository.save(newPayment)
    }

    /* 입금 처리 */
    @Transactional
    fun pay(id: Long) {
        val now = LocalDateTime.now()
        val payment = paymentRepository.findById(id)
            .orElseThrow { IllegalStateException("Payment not found") }

        payment.pay(now);
        paymentRepository.save(payment);

        /* 결제 완료 이벤트 저장 */
        val event = PaymentCompletedEvent(id)
        eventRepository.save(
            EventEntity(
                type = event.javaClass.simpleName,
                payload = jsonMapper.writeValueAsString(event),
                registrationDate = now
            )
        )
    }

    /* 실패 처리 */
    fun fail(id: Long) {
        val now = LocalDateTime.now()
        val payment = paymentRepository.findById(id)
            .orElseThrow { IllegalStateException("Payment not found") }

        payment.fail();
        paymentRepository.save(payment);
    }

}