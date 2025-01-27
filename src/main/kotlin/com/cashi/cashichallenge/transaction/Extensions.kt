package com.cashi.cashichallenge.transaction

import com.cashi.cashichallenge.fee.toDTO
import com.cashi.cashichallenge.transaction.dto.TransactionDTO
import com.cashi.cashichallenge.transaction.dto.TransactionRequest

fun TransactionRequest.toEntity() = Transaction(
    amount = this.amount,
    asset = this.asset,
    type = this.type,
    state = TransactionState.SettledPendingFee,
    assetType = this.assetType
)

fun Transaction.toDTO() = TransactionDTO(
    id = this.id,
    amount = this.amount,
    asset = this.asset,
    type = this.type,
    state = this.state,
    assetType = this.assetType,
    createdAt = this.createdAt,
    fees = this.fees.map { it.toDTO() }
)