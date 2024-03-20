package com.cashi.cashichallenge.fee.unit

import com.cashi.cashichallenge.fee.service.StandardFeeRate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal


class StandardFeeRateTest {
    private val service = StandardFeeRate()

    @ParameterizedTest
    @CsvSource(
        "0,0.0000",
        "0.1,0.00015",
        "1,0.0015",
        "10,0.0150",
        "100,0.1500",
        "1000,1.5000"
    )
    fun shouldCalculateStandardFeeRate(amount: BigDecimal, expectedFee: BigDecimal) {
        assertThat(service.calculate(amount)).isEqualTo(expectedFee)
    }
}