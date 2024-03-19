package com.cashi.cashichallenge.fee.dto

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.fee.FeeType
import java.math.BigDecimal

data class FeeDTO(
    val fee: BigDecimal,
    val asset: Asset,
    val rate: BigDecimal,
    val type: FeeType
)
