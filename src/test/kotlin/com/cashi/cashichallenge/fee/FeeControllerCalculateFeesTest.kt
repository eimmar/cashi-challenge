package com.cashi.cashichallenge.fee

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.transaction.Transaction
import com.cashi.cashichallenge.transaction.TransactionRepository
import com.cashi.cashichallenge.transaction.TransactionState
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
class FeeControllerCalculateFeesTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    @AfterEach
    fun beforeAndAfterEach() = transactionRepository.deleteAll()

    @Test
    fun shouldCalculateMobileTopUpTransactionFees() {
        setUpFixtures(TransactionState.SettledPendingFee, TransactionType.MobileTopUp)
        val id = transactionRepository.findAll().first().id

        val result = callFeeCalculationEndpoint(id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        val transaction = transactionRepository.findById(id).orElseThrow()
        assertThat(transaction.fees).hasSize(1)

        transaction.fees[0].let {
            assertThat(it.amount).isEqualTo(BigDecimal("0.0150"))
            assertThat(it.rate).isEqualTo(BigDecimal("0.0015"))
            assertThat(it.type).isEqualTo(FeeType.Standard)
        }
    }

    @Test
    fun shouldCalculateWireTransferTransactionFees() {
        setUpFixtures(TransactionState.SettledPendingFee, TransactionType.WireTransfer)
        val id = transactionRepository.findAll().first().id

        val result = callFeeCalculationEndpoint(id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        val transaction = transactionRepository.findById(id).orElseThrow()
        assertThat(transaction.fees).hasSize(2)

        transaction.fees[0].let {
            assertThat(it.amount).isEqualTo(BigDecimal("4.0000"))
            assertThat(it.rate).isEqualTo(BigDecimal("0.0300"))
            assertThat(it.type).isEqualTo(FeeType.Commission)
        }

        transaction.fees[1].let {
            assertThat(it.amount).isEqualTo(BigDecimal("0.0150"))
            assertThat(it.rate).isEqualTo(BigDecimal("0.0015"))
            assertThat(it.type).isEqualTo(FeeType.Standard)
        }
    }

    @Test
    fun shouldRecalculateOnlyMissingFees() {
        setUpFixtures(TransactionState.SettledPendingFee, TransactionType.WireTransfer, listOf(FeeType.Standard))
        val id = transactionRepository.findAll().first().id

        val result = callFeeCalculationEndpoint(id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        val transaction = transactionRepository.findById(id).orElseThrow()
        assertThat(transaction.fees).hasSize(2)

        transaction.fees[0].let {
            assertThat(it.amount).isEqualTo(BigDecimal("1.0000"))
            assertThat(it.rate).isEqualTo(BigDecimal("0.1500"))
            assertThat(it.type).isEqualTo(FeeType.Standard)
        }

        transaction.fees[1].let {
            assertThat(it.amount).isEqualTo(BigDecimal("4.0000"))
            assertThat(it.rate).isEqualTo(BigDecimal("0.0300"))
            assertThat(it.type).isEqualTo(FeeType.Commission)
        }
    }

    @Test
    fun shouldFailWhenTransactionDoesNotExist() {
        setUpFixtures(TransactionState.SettledPendingFee, TransactionType.WireTransfer)

        val result = callFeeCalculationEndpoint(0)
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldFailWhenTransactionIsAlreadyCharged() {
        setUpFixtures(TransactionState.Charged, TransactionType.WireTransfer)
        val id = transactionRepository.findAll().first().id

        val result = callFeeCalculationEndpoint(id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    private fun setUpFixtures(
        state: TransactionState,
        type: TransactionType,
        feeTypes: List<FeeType> = emptyList()
    ): Transaction {
        val transaction = Transaction(
            amount = BigDecimal("10"),
            asset = Asset.EUR,
            type = type,
            state = state,
            assetType = AssetType.FIAT,
        )

        feeTypes.forEach {
            transaction.fees.add(
                Fee(
                    transaction = transaction,
                    amount = BigDecimal("1"),
                    rate = BigDecimal("0.15"),
                    asset = Asset.EUR,
                    type = it
                )
            )
        }

        transactionRepository.save(transaction)

        return transaction
    }

    private fun callFeeCalculationEndpoint(transactionId: Long) =
        restTemplate.postForEntity("/fee/transaction/{transactionId}/calculate", "", String::class.java, transactionId)
}