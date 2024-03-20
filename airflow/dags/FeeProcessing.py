from __future__ import annotations
from datetime import datetime, timedelta
from airflow.models.dag import DAG
from airflow.providers.http.operators.http import SimpleHttpOperator
from airflow.models.param import Param


def handleResponse(response):
    return response.status_code == 200

with DAG(
        "FeeProcessing",
        default_args={
            "depends_on_past": False,
            "email": ["airflow@example.com"],
            "email_on_failure": False,
            "email_on_retry": False,
            "retries": 3,
            "retry_delay": timedelta(seconds=10)
        },
        description="Transaction fee calculation workflow",
        schedule=None,
        start_date=datetime(2024, 3, 20),
        catchup=False,
        tags=["Fees"],
        params={
            "transactionId": Param(type="integer", minimum=1)
        },
) as dag:
    dag.doc_md = "This DAG calculates fees and charges the transaction."

calculateTransactionFeeTask = SimpleHttpOperator(
    task_id="calculateTransactionFee",
    method="POST",
        http_conn_id="cashi_challenge_api",
    endpoint="/fee/transaction/{{ params.transactionId }}/calculate",
    response_check=lambda response: handleResponse(response),
    dag=dag
)

chargeTransactionTask = SimpleHttpOperator(
    task_id="chargeTransaction",
    method="POST",
    http_conn_id="cashi_challenge_api",
    endpoint="/transaction/{{ params.transactionId }}/charge",
    response_check=lambda response: handleResponse(response),
    dag=dag
)

calculateTransactionFeeTask >> chargeTransactionTask
