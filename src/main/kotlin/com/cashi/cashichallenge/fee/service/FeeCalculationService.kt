package com.cashi.cashichallenge.fee.service

import com.cashi.cashichallenge.fee.Fee
import com.cashi.cashichallenge.fee.FeeRepository
import com.cashi.cashichallenge.transaction.Transaction
import org.springframework.stereotype.Service

@Service
class FeeCalculationService(private val feeRates: List<FeeRate>, private val feeRepository: FeeRepository) {
    fun calculateFees(transaction: Transaction) {
        val newFees = feeRates.filter { shouldCalculateFee(it, transaction) }
            .map {
                Fee(
                    transaction = transaction,
                    amount = it.calculate(transaction.amount),
                    rate = it.rate,
                    asset = transaction.asset,
                    type = it.type
                )
            }

        feeRepository.saveAll(newFees)
    }

    private fun shouldCalculateFee(feeRate: FeeRate, transaction: Transaction): Boolean {
        val appliedFees = transaction.fees.map { it.type }

        return feeRate.transactionTypes.contains(transaction.type) && !appliedFees.contains(feeRate.type)
    }
}