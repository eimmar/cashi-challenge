package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.fee.FeeType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"]
)
class TransactionControllerChargeTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    @AfterEach
    fun beforeAndAfterEach() = transactionRepository.deleteAll()

    @Test
    fun shouldChargeTransactionWhenRequestIsValid() {
        setUpFixtures()
        val id = transactionRepository.findAll().first().id

        val result = callTransactionChargeEndpoint(id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        val transaction = transactionRepository.findById(id).orElseThrow()
        assertThat(transaction.state).isEqualTo(TransactionState.Charged)
    }

    @Test
    fun shouldReturnNotFoundWhenIdIsInvalid() {
        setUpFixtures()

        val result = callTransactionChargeEndpoint(0)
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

        transactionRepository.save(transaction)
    }

    private fun callTransactionChargeEndpoint(id: Long) =
        restTemplate.postForEntity("/transaction/{id}/charge", "", String::class.java, id)
}