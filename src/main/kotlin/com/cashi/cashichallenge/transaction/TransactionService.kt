package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.airflow.AirflowClient
import com.cashi.cashichallenge.common.exception.DataNotFoundException
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val airflowClient: AirflowClient
) {
    fun place(transactionRequest: TransactionRequest): Transaction {
        val transaction = transactionRepository.save(transactionRequest.toEntity())
        airflowClient.triggerFeeCalculationDAG(transaction.id)

        return transaction
    }

    fun findById(id: Long): Transaction {
        return transactionRepository.findByIdOrNull(id) ?: throw DataNotFoundException("Transaction(id=$id) not found.")
    }

    fun findTransactionWithPendingFees(id: Long): Transaction {
        return transactionRepository.findByIdAndState(id, TransactionState.SettledPendingFee)
            ?: throw DataNotFoundException("Transaction(id=$id) with pending fees not found.")
    }

    fun charge(id: Long) {
        val transaction = findById(id)
        transaction.state = TransactionState.Charged

        transactionRepository.save(transaction)
    }
}