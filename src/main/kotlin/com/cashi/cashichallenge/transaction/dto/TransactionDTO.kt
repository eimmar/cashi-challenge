package com.cashi.cashichallenge.transaction.dto

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.transaction.TransactionState
import com.cashi.cashichallenge.transaction.TransactionType
import java.math.BigDecimal
import java.time.OffsetDateTime

data class TransactionDTO(
    val id: Long,
    val amount: BigDecimal,
    val asset: Asset,
    val assetType: AssetType,
    val type: TransactionType,
    val state: TransactionState,
    val createdAt: OffsetDateTime,
)
