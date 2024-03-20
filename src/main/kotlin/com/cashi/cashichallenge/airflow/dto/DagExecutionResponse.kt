package com.cashi.cashichallenge.airflow.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DagExecutionResponse(@JsonProperty("dag_run_id") val dagRunId: String)