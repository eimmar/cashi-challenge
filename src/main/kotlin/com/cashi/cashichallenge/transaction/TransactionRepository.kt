package com.cashi.cashichallenge.transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository: JpaRepository<Transaction, Long> {
    fun findByIdAndState(id: Long, state: TransactionState): Transaction?
}