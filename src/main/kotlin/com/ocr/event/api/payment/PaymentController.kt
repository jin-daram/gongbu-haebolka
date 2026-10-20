package com.ocr.event.api.payment

import com.ocr.event.api.payment.request.PaymentRequest
import com.ocr.event.service.payment.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payment")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/request")
    fun requestPayment(@RequestBody request: PaymentRequest) : ResponseEntity<Void> {
        paymentService.requestPayment(request.amount, request.type);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentId}/pay")
    fun pay(@PathVariable paymentId: Long) : ResponseEntity<Void> {
        paymentService.pay(paymentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentId}/fail")
    fun fail(@PathVariable paymentId: Long) : ResponseEntity<Void> {
        paymentService.fail(paymentId);
        return ResponseEntity.ok().build();
    }

}