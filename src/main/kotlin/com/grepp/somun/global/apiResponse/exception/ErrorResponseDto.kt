package com.grepp.somun.global.apiResponse.exception



import org.springframework.http.HttpStatus

data class ErrorResponseDto(
    val httpStatus: HttpStatus? = null,
    val isSuccess: Boolean,
    val code: String,
    val message: String
)
