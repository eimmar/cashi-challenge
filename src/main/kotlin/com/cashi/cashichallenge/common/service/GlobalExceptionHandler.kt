package com.cashi.cashichallenge.common.service;

import com.cashi.cashichallenge.common.exception.DataNotFoundException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler
    fun handleNotFoundException(ex: DataNotFoundException) = ResponseEntity.notFound()
}