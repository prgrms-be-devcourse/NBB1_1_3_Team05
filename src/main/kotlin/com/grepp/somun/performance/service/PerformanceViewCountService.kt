package com.grepp.somun.performance.service

import com.grepp.somun.performance.dto.response.PerformanceListResponse


interface PerformanceViewCountService {
    fun incrementViewCount(performanceId: Long)

    fun getPopularPerformances(): PerformanceListResponse
}