package com.grepp.somun.performance.dto.response

import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.domain.PerformanceDetail
import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

data class PerformanceDetailResponse(
    val memberName: String,
    val performanceId: Long,
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val description: String,
    val maxAudience: Int,
    val address: String,
    val location: String,
    val imageUrl: String?,
    val price: Int,
    val remainingTickets: Int?,
    val startDate: LocalDateTime?,
    val status: PerformanceStatus,
    val isUpdatable: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val categories: List<CategoryDto>?
) {
    companion object {
        fun from(updatable: Boolean, performanceDetail: PerformanceDetail): PerformanceDetailResponse {
            val categoryDtos = performanceDetail.categories?.map { CategoryDto.toDto(it) }
            return PerformanceDetailResponse(
                memberName = performanceDetail.memberName!!,
                performanceId = performanceDetail.performanceId!!,
                title = performanceDetail.title!!,
                dateStartTime = performanceDetail.dateStartTime!!,
                dateEndTime = performanceDetail.dateEndTime!!,
                description = performanceDetail.description!!,
                maxAudience = performanceDetail.maxAudience!!,
                address = performanceDetail.address!!,
                location = performanceDetail.location!!,
                imageUrl = performanceDetail.imageUrl,
                price = performanceDetail.price!!,
                remainingTickets = performanceDetail.remainingTickets,
                startDate = performanceDetail.startDate,
                status = performanceDetail.status!!,
                isUpdatable = updatable,
                createdAt = performanceDetail.createdAt!!,
                updatedAt = performanceDetail.updatedAt!!,
                categories = categoryDtos
            )
        }
    }
}