package com.cashi.cashichallenge.fee

import com.cashi.cashichallenge.fee.service.FeeCalculationService
import com.cashi.cashichallenge.transaction.TransactionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fee")
class FeeController(val feeCalculationService: FeeCalculationService, val transactionService: TransactionService) {
    @PostMapping("/transaction/{transactionId}/calculate")
    fun calculateFees(@PathVariable transactionId: Long) {
        transactionService.findTransactionWithPendingFees(transactionId)
            ?.let { feeCalculationService.calculateFees(it) }
    }
}
