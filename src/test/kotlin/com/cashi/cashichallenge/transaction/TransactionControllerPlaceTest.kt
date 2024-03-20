package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.transaction.dto.TransactionPlacedResponse
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.temporal.ChronoUnit

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"]
)
class TransactionControllerPlaceTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    @AfterEach
    fun beforeAndAfterEach() = transactionRepository.deleteAll()

    @Test
    fun shouldPlaceTransactionWhenRequestIsValid() {
        val requestBody = TransactionRequest(
            amount = BigDecimal("0.01"),
            asset = Asset.EUR,
            assetType = AssetType.FIAT,
            type = TransactionType.MobileTopUp
        )
        val result = callTransactionPlaceEndpoint(requestBody)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body?.id).isNotNull

        val transaction = transactionRepository.findById(result.body!!.id).orElseThrow()
        assertThat(transaction.state).isEqualTo(TransactionState.SettledPendingFee)
        assertThat(transaction.amount).isEqualTo(BigDecimal.valueOf(0.01))
        assertThat(transaction.asset).isEqualTo(Asset.EUR)
        assertThat(transaction.assetType).isEqualTo(AssetType.FIAT)
        assertThat(transaction.type).isEqualTo(TransactionType.MobileTopUp)
        assertThat(transaction.fees).containsExactlyElementsOf(emptyList<Fee>())
        assertThat(transaction.createdAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        assertThat(transaction.updatedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun shouldFailWhenRequestIsInvalid() {
        val requestBody = TransactionRequest(
            amount = BigDecimal("0.00999"),
            asset = Asset.EUR,
            assetType = AssetType.FIAT,
            type = TransactionType.MobileTopUp
        )
        val result = callTransactionPlaceEndpoint(requestBody)

        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(transactionRepository.findById(result.body!!.id).isEmpty).isTrue
    }

    private fun callTransactionPlaceEndpoint(request: TransactionRequest) =
        restTemplate.postForEntity("/transaction", request, TransactionPlacedResponse::class.java)
}