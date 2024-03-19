package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.transaction.dto.TransactionDTO
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import com.cashi.cashichallenge.transaction.dto.TransactionPlacedResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController("/transaction/fee")
class TransactionController(val transactionService: TransactionService) {
    @PostMapping
    fun place(@RequestBody transactionRequest: @Valid TransactionRequest): TransactionPlacedResponse {
        val transaction = transactionService.place(transactionRequest)

        return TransactionPlacedResponse(transaction.id)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): TransactionDTO {
        return transactionService.findById(id).toDTO()
    }
}
