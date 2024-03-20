package com.cashi.cashichallenge.transaction.dto

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class TransactionRequest(
    @field:DecimalMin(value = "0.01")
    val amount: BigDecimal,
    val asset: Asset,
    val assetType: AssetType,
    val type: TransactionType
)
