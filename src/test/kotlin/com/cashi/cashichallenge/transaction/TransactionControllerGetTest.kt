package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.fee.FeeType
import com.cashi.cashichallenge.fee.dto.FeeDTO
import com.cashi.cashichallenge.transaction.dto.TransactionDTO
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
class TransactionControllerGetTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    @AfterEach
    fun beforeAndAfterEach() = transactionRepository.deleteAll()

    @Test
    fun shouldReturnAllTransactionInformationWithFees() {
        setUpFixtures()
        val id = transactionRepository.findAll().first().id

        val result = callTransactionGetEndpoint(id, TransactionDTO::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        val transaction = result.body!!
        assertThat(transaction.state).isEqualTo(TransactionState.Charged)
        assertThat(transaction.amount).isEqualTo(BigDecimal("10.00"))
        assertThat(transaction.asset).isEqualTo(Asset.EUR)
        assertThat(transaction.assetType).isEqualTo(AssetType.FIAT)
        assertThat(transaction.type).isEqualTo(TransactionType.MobileTopUp)
        assertThat(transaction.fees).containsExactlyElementsOf(
            listOf(
                FeeDTO(
                    fee = BigDecimal("1.00"),
                    asset = Asset.EUR,
                    rate = BigDecimal("0.15"),
                    type = FeeType.Standard
                )
            )
        )
        assertThat(transaction.createdAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
    }

    @Test
    fun shouldReturnNotFoundWhenIdIsInvalid() {
        setUpFixtures()

        val result = callTransactionGetEndpoint(0, String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    private fun setUpFixtures() {
        val transaction = Transaction(
            amount = BigDecimal("10"),
            asset = Asset.EUR,
            type = TransactionType.MobileTopUp,
            state = TransactionState.Charged,
            assetType = AssetType.FIAT,
        )

        val fee = Fee(
            transaction = transaction,
            amount = BigDecimal("1"),
            rate = BigDecimal("0.15"),
            asset = Asset.EUR,
            type = FeeType.Standard
        )

        transaction.fees.add(fee)

        transactionRepository.save(transaction)
    }

    private fun <T> callTransactionGetEndpoint(id: Long, responseType: Class<T>) =
        restTemplate.getForEntity("/transaction/{id}", responseType, id)
}