package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.common.exception.DataNotFoundException
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TransactionService(val transactionRepository: TransactionRepository) {
    fun place(transactionRequest: TransactionRequest): Transaction {
        return transactionRepository.save(transactionRequest.toEntity())
    }

    fun findById(id: Long): Transaction {
        return transactionRepository.findByIdOrNull(id) ?: throw DataNotFoundException("Transaction(id=$id) not found.")
    }
}