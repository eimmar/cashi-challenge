package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "financial_transaction")
class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToMany
    val fees: List<Fee> = listOf(),

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val asset: Asset,

    @Column(nullable = false)
    val assetType: AssetType,

    @Column(nullable = false)
    val type: TransactionType,

    @Column(nullable = false)
    val state: TransactionState,

    @Column(nullable = false)
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

enum class TransactionType {
    MobileTopUp, WireTransfer
}

enum class TransactionState {
    Pending, SettledPendingFee, Completed, Failed
}
