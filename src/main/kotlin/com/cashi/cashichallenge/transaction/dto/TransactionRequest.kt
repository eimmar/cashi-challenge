package com.cashi.cashichallenge.transaction.dto

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.transaction.Transaction
import com.cashi.cashichallenge.transaction.TransactionState
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class TransactionRequest(
    @DecimalMin(value = "0.01")
    val amount: BigDecimal,
    val asset: Asset,
    val assetType: AssetType,
    val type: TransactionType
)

fun TransactionRequest.toEntity() = Transaction(
    amount = this.amount,
    asset = this.asset,
    type = this.type,
    state = TransactionState.Pending,
    assetType = this.assetType
)