package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import jakarta.persistence.*
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "transaction")
class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToMany(mappedBy = "transaction", cascade = [CascadeType.PERSIST, CascadeType.REMOVE])
    val fees: MutableList<Fee> = mutableListOf(),

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val asset: Asset,

    @Column(nullable = false)
    val assetType: AssetType,

    @Column(nullable = false)
    val type: TransactionType,

    @Column(nullable = false)
    var state: TransactionState,

    @Column(nullable = false)
    @CreatedDate
    val createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    @UpdateTimestamp
    val updatedAt: OffsetDateTime = OffsetDateTime.now()
)

enum class TransactionState {
    SettledPendingFee, Charged
}
