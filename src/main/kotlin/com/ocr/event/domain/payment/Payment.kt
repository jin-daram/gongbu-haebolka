package com.ocr.event.domain.payment

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "tb_payment")
class Payment(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus,

    @Enumerated(EnumType.STRING)
    val type: PaymentType,

    val amount: BigDecimal,

    @Column(nullable = true)
    var paidDate: LocalDateTime?,

    val registrationDate: LocalDateTime

) {

    fun pay(paidDate: LocalDateTime) {
        this.status = PaymentStatus.SUCCESS
        this.paidDate = paidDate
    }

    fun fail() {
        this.status = PaymentStatus.FAILED
    }

}