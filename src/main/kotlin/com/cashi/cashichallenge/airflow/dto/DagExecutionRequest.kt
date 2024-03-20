package com.cashi.cashichallenge.airflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DagExecutionRequest<T>(@JsonProperty("dag_run_id") val dagRunId: String, val conf: T)
