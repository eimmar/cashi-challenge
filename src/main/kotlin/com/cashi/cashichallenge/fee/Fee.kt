package com.cashi.cashichallenge.fee

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.transaction.Transaction
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "fee")
class Fee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    val transaction: Transaction,

    @Column(nullable = false, precision = 38, scale = 4)
    val amount: BigDecimal,

    @Column(nullable = false)
    val asset: Asset,

    @Column(nullable = false, precision = 5, scale = 4)
    val rate: BigDecimal,

    val type: FeeType,

    @Column(nullable = false)
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

enum class FeeType {
    Standard, Commission
}
