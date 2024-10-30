package com.grepp.somun.global.apiResponse.exception



interface BaseErrorCode {
    fun getResponseWithHttpStatus(): ErrorResponseDto
}
