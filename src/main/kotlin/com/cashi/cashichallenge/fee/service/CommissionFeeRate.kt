package com.cashi.cashichallenge.fee.service

import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.FeeType
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class CommissionFeeRate : FeeRate {
    override val transactionTypes: List<TransactionType>
        get() = listOf(TransactionType.WireTransfer)

    override val type: FeeType
        get() = FeeType.Commission

    override val rate: BigDecimal
        get() = BigDecimal("0.3")

    override fun calculate(amount: BigDecimal): BigDecimal = maxOf(amount.multiply(rate), BigDecimal("4"))
}