package com.grepp.somun.performance.dto.response

data class PerformanceUpdateResponse(val performanceId: Long) {
    companion object {
        fun from(performanceId: Long): PerformanceUpdateResponse {
            return PerformanceUpdateResponse(performanceId)
        }
    }
}