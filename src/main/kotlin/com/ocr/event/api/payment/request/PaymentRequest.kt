package com.ocr.event.api.payment.request

import com.ocr.event.domain.payment.PaymentType
import java.math.BigDecimal

data class PaymentRequest(
    val amount: BigDecimal,
    val type: PaymentType,
)