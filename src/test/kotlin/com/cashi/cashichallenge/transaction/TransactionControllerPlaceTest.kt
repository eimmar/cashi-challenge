package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.common.enums.Asset
import com.cashi.cashichallenge.common.enums.AssetType
import com.cashi.cashichallenge.common.enums.TransactionType
import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.transaction.dto.TransactionPlacedResponse
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import java.math.BigDecimal
import java.time.temporal.ChronoUnit


@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true", "airflow.url=http://localhost:8070/api/v1"]
)
@ExtendWith(WireMockExtension::class)
class TransactionControllerPlaceTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    private val airflowUrl = "/api/v1/dags/FeeProcessing/dagRuns"

    companion object {
        private val airflowAPIMock = WireMockServer(WireMockConfiguration.options().port(8070))

        @JvmStatic
        @AfterAll
        fun afterAll() {
            airflowAPIMock.stop()
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            airflowAPIMock.start()
        }
    }

    @BeforeEach
    @AfterEach
    fun beforeAndAfterEach() {
        transactionRepository.deleteAll()
        airflowAPIMock.resetAll()
    }

    @Test
    fun shouldPlaceTransactionWhenRequestIsValid() {
        mockAirflowSuccess()

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
        assertThat(transaction.amount).isEqualTo(BigDecimal("0.0100"))
        assertThat(transaction.asset).isEqualTo(Asset.EUR)
        assertThat(transaction.assetType).isEqualTo(AssetType.FIAT)
        assertThat(transaction.type).isEqualTo(TransactionType.MobileTopUp)
        assertThat(transaction.fees).containsExactlyElementsOf(emptyList<Fee>())
        assertThat(transaction.createdAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))
        assertThat(transaction.updatedAt).isCloseToUtcNow(within(1, ChronoUnit.SECONDS))

        airflowAPIMock.verify(
            1, postRequestedFor(urlPathEqualTo(airflowUrl))
                .withRequestBody(matchingJsonPath("$.conf.transactionId", equalTo(result.body?.id.toString())))
        )
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

    @Test
    fun shouldFailWhenAirflowRequestFailsButStillPlaceTransaction() {
        mockAirflowError()
        val requestBody = TransactionRequest(
            amount = BigDecimal("1"),
            asset = Asset.EUR,
            assetType = AssetType.FIAT,
            type = TransactionType.MobileTopUp
        )
        val result = callTransactionPlaceEndpoint(requestBody)

        assertThat(result.statusCode).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
        assertThat(transactionRepository.count()).isOne
    }

    private fun callTransactionPlaceEndpoint(request: TransactionRequest) =
        restTemplate.postForEntity("/transaction", request, TransactionPlacedResponse::class.java)

    private fun mockAirflowSuccess() {
        airflowAPIMock.stubFor(
            post(urlPathEqualTo(airflowUrl))
                .willReturn(
                    aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                    {
                      "conf": {
                        "transactionId": 1
                      },
                      "dag_id": "FeeProcessing",
                      "dag_run_id": "transaction_6",
                      "data_interval_end": "2024-03-20T15:05:08.684395+00:00",
                      "data_interval_start": "2024-03-20T15:05:08.684395+00:00",
                      "end_date": null,
                      "execution_date": "2024-03-20T15:05:08.684395+00:00",
                      "external_trigger": true,
                      "last_scheduling_decision": null,
                      "logical_date": "2024-03-20T15:05:08.684395+00:00",
                      "note": null,
                      "run_type": "manual",
                      "start_date": null,
                      "state": "queued"
                    }
            """.trimIndent()
                        )
                )
        )
    }

    private fun mockAirflowError() {
        airflowAPIMock.stubFor(
            post(urlPathEqualTo(airflowUrl))
                .willReturn(
                    aResponse().withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                            """
                            {
                              "detail": "string",
                              "instance": "string",
                              "status": 0,
                              "title": "string",
                              "type": "string"
                            }
            """.trimIndent()
                        )
                )
        )
    }
}