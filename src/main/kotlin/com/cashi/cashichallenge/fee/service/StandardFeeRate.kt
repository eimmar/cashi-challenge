package com.cashi.cashichallenge.fee.service

import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.FeeType
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class StandardFeeRate: FeeRate {
    override val transactionTypes: List<TransactionType>
        get() = listOf(TransactionType.WireTransfer, TransactionType.MobileTopUp)

    override val type: FeeType
        get() = FeeType.Standard

    override val rate: BigDecimal
        get() = BigDecimal("0.0015")

    override fun calculate(amount: BigDecimal): BigDecimal = amount.multiply(rate)
}