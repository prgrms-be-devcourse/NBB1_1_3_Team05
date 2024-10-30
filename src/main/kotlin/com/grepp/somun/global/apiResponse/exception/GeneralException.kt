package com.grepp.somun.global.apiResponse.exception


class GeneralException(val code: BaseErrorCode) : RuntimeException() {
    val sourceClass: String
    val sourceMethod: String
    val sourcePackage: String
    val sourceAddress: String

    init {
        val element = this.stackTrace.getOrNull(1) ?: throw IllegalStateException("Stack trace is empty")
        sourceClass = element.className
        sourceMethod = element.methodName
        sourcePackage = sourceClass.substringBeforeLast('.', "Unknown")
        sourceAddress = "$sourcePackage.$sourceClass.$sourceMethod"
    }

    val errorReasonHttpStatus: ErrorResponseDto
        get() = code.getResponseWithHttpStatus()
}
