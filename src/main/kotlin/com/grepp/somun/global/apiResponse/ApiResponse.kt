package com.grepp.somun.global.apiResponse


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.grepp.somun.global.apiResponse.success.SuccessStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@JsonPropertyOrder("isSuccess", "code", "message", "result")
data class ApiResponse<T>(
    @JsonProperty("isSuccess") val isSuccess: Boolean,
    val httpStatus: HttpStatus,
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL) val result: T? = null
) {
    companion object {
        fun <T> onSuccess(result: T): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity.ok(
                ApiResponse(true, HttpStatus.OK, "COMMON200", SuccessStatus._OK.message, result)
            )
        }

        fun <T> onSuccess(httpStatus: HttpStatus, code: String, message: String, result: T): ResponseEntity<ApiResponse<T>> {
            return ResponseEntity(
                ApiResponse(true, httpStatus, code, message, result), httpStatus
            )
        }

        fun onSuccess(): ResponseEntity<ApiResponse<Void>> {
            return ResponseEntity.ok(
                ApiResponse(true, HttpStatus.OK, "COMMON200", SuccessStatus._OK.message, null)
            )
        }

        fun <T> onFailure(httpStatus: HttpStatus, code: String, message: String, data: T): ApiResponse<T> {
            return ApiResponse(false, httpStatus, code, message, data)
        }
    }
}
