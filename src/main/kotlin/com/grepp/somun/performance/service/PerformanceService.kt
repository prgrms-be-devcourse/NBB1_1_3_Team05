package com.grepp.somun.performance.service

import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.request.PerformanceRegisterRequest
import com.grepp.somun.performance.dto.request.PerformanceUpdateRequest
import com.grepp.somun.performance.dto.response.PerformanceDetailResponse
import com.grepp.somun.performance.dto.response.PerformanceListResponse
import com.grepp.somun.performance.dto.response.PerformanceRegisterResponse
import com.grepp.somun.performance.dto.response.PerformanceUpdateResponse
import org.springframework.web.multipart.MultipartFile

interface PerformanceService {
    fun registerPerformance(
        email: String,
        performanceRegisterRequest: PerformanceRegisterRequest,
        imageFile: MultipartFile?
    ): PerformanceRegisterResponse

    fun getPerformanceList(
        page: Int,
        size: Int,
        categoryId: Long?,
        search: String?,
        email: String?
    ): PerformanceListResponse

    fun getPerformanceDetail(email: String, performanceId: Long): PerformanceDetailResponse

    fun updatePerformance(
        email: String,
        performanceId: Long,
        performanceUpdateRequest: PerformanceUpdateRequest
    ): PerformanceUpdateResponse

    fun deletePerformance(email: String, performanceId: Long)

    fun getMyPerformanceList(email: String, page: Int, size: Int): PerformanceListResponse

    fun getCategoryList(): List<CategoryDto>

    fun getPerformanceListByUserCategories(email: String): PerformanceListResponse

    fun getPopularPerformances(performanceIds: List<Long>): PerformanceListResponse
}