package com.cashi.cashichallenge.fee

import com.cashi.cashichallenge.fee.dto.FeeDTO

fun Fee.toDTO() = FeeDTO(
    fee = this.amount,
    asset = this.asset,
    rate = this.rate,
    type = this.type
)