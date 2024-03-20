package com.cashi.cashichallenge.airflow

import com.cashi.cashichallenge.airflow.dto.DagExecutionRequest
import com.cashi.cashichallenge.airflow.dto.DagExecutionResponse
import com.cashi.cashichallenge.airflow.dto.FeeProcessingRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Service
class AirflowClient(
    @Value("\${airflow.url}") private val url: String,
    @Value("\${airflow.user}") private val user: String,
    @Value("\${airflow.password}") private val password: String
) {
    internal val client: RestTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .rootUri(url)
        .build()

    fun triggerFeeCalculationDAG(transactionId: Long) {
        val requestBody = DagExecutionRequest(
            dagRunId = "transaction_${transactionId}_${System.currentTimeMillis()}",
            conf = FeeProcessingRequest(transactionId)
        )
        val headers = HttpHeaders()
        headers.setBasicAuth(user, password)

        client.exchange(
            "/dags/FeeProcessing/dagRuns",
            HttpMethod.POST,
            HttpEntity(requestBody, headers),
            DagExecutionResponse::class.java
        )
    }
}