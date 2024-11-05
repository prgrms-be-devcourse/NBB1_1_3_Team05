package com.grepp.somun.performance.dto.response



data class PerformanceQueueResponse(
    val userEmail: String,
    val rank: Long
) {
    companion object {
        fun from(userEmail: String, rank: Long): PerformanceQueueResponse {
            return PerformanceQueueResponse(userEmail, rank)
        }
    }
}
