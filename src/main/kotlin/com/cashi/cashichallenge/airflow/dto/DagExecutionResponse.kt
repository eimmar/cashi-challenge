package com.cashi.cashichallenge.airflow.dto

data class DagExecutionResponse(
    val detail: String,
    val instance: String?,
    val status: Int,
    val title: String,
    val type: String
)