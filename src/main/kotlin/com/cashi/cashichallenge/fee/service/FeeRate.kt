package com.cashi.cashichallenge.fee.service

import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.FeeType
import java.math.BigDecimal

interface FeeRate {
    val transactionTypes: List<TransactionType>

    val type: FeeType

    val rate: BigDecimal

    fun calculate(amount: BigDecimal): BigDecimal
}