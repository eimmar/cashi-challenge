package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.transaction.dto.TransactionDTO
import com.cashi.cashichallenge.transaction.dto.TransactionPlacedResponse
import com.cashi.cashichallenge.transaction.dto.TransactionRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transaction")
class TransactionController(private val transactionService: TransactionService) {
    @PostMapping
    fun place(@Valid @RequestBody transactionRequest: TransactionRequest): TransactionPlacedResponse {
        val transaction = transactionService.place(transactionRequest)

        return TransactionPlacedResponse(transaction.id)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): TransactionDTO {
        return transactionService.findById(id).toDTO()
    }

    @PostMapping("/{id}/charge")
    fun charge(@PathVariable id: Long) {
        transactionService.charge(id)
    }
}
