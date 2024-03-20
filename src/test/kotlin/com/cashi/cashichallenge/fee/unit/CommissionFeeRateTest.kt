package com.cashi.cashichallenge.fee.unit

import com.cashi.cashichallenge.fee.service.CommissionFeeRate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal


class CommissionFeeRateTest {
    private val service = CommissionFeeRate()

    @ParameterizedTest
    @CsvSource(
        "0,4",
        "0.3,4",
        "133.3333,4",
        "133.3334,4.000002",
        "1000,30.00"
    )
    fun shouldCalculateStandardFeeRate(amount: BigDecimal, expectedFee: BigDecimal) {
        assertThat(service.calculate(amount)).isEqualTo(expectedFee)
    }
}