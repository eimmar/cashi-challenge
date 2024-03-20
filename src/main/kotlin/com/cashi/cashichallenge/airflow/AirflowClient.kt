package com.cashi.cashichallenge.airflow

import com.cashi.cashichallenge.airflow.dto.DagExecutionRequest
import com.cashi.cashichallenge.airflow.dto.DagExecutionResponse
import com.cashi.cashichallenge.airflow.dto.FeeProcessingRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Service
class AirflowClient(@Value("\${airflow.url}") private val url: String) {
    internal val client: RestTemplate = RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .rootUri(url)
        .build()

    fun triggerFeeCalculationDAG(transactionId: Long) {
        val request = DagExecutionRequest(
            dagRunId = "transaction_${transactionId}_${System.currentTimeMillis()}",
            conf = FeeProcessingRequest(transactionId)
        )

        client.postForEntity("/dags/FeeProcessing/dagRuns", request, DagExecutionResponse::class.java)
    }
}