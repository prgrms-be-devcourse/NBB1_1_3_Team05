package com.grepp.somun.global.apiResponse.exception


import com.grepp.somun.global.apiResponse.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice(annotations = [RestController::class])
class ExceptionAdvice : ResponseEntityExceptionHandler() {

    private val logger: Logger = LoggerFactory.getLogger(ExceptionAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun exception(e: Exception): ResponseEntity<ApiResponse<Any?>> {
        logger.error("Unhandled exception occurred: ${e.message}", e)
        val body = ApiResponse.onFailure<Any?>(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "처리되지 않은 오류가 발생했습니다.",
            null
        )
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(GeneralException::class)
    fun onThrowException(generalException: GeneralException): ResponseEntity<ApiResponse<Any?>> {
        val errorResponse = generalException.errorReasonHttpStatus
        val body = ApiResponse.onFailure<Any?>(
            errorResponse.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR,
             errorResponse.code,
            errorResponse.message,
            null
        )
        return ResponseEntity(body, errorResponse.httpStatus ?: HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
