package com.cashi.cashichallenge.fee

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeeRepository: JpaRepository<Fee, Long>