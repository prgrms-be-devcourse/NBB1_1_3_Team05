package com.grepp.somun.performance.dto.response

import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.domain.PerformanceWithCategory
import java.time.LocalDateTime

data class PerformanceListResponse(
    val totalElements: Long,
    val performanceList: List<PerformanceList>
) {
    data class PerformanceList(
        val memberName: String,
        val performanceId: Long,
        val title: String,
        val dateStartTime: LocalDateTime,
        val dateEndTime: LocalDateTime,
        val address: String,
        val imageUrl: String?,
        val price: Int,
        val status: String,
        val remainingTicket: Int?,
        val categories: List<CategoryDto>?
    ) {
        companion object {
            fun from(performanceWithCategory: PerformanceWithCategory): PerformanceList {
                val categoryDtos = performanceWithCategory.categories?.map { CategoryDto.toDto(it) }

                return PerformanceList(
                    memberName = performanceWithCategory.memberName!!,
                    performanceId = performanceWithCategory.performanceId!!,
                    title = performanceWithCategory.title!!,
                    dateStartTime = performanceWithCategory.dateStartTime!!,
                    dateEndTime = performanceWithCategory.dateEndTime!!,
                    address = performanceWithCategory.address!!,
                    imageUrl = performanceWithCategory.imageUrl,
                    price = performanceWithCategory.price,
                    status = performanceWithCategory.status.toString(),
                    remainingTicket = performanceWithCategory.remainingTicket,
                    categories = categoryDtos
                )
            }
        }
    }

    companion object {
        fun from(totalElements: Long, performanceWithCategory: List<PerformanceWithCategory>): PerformanceListResponse {
            val performanceList = performanceWithCategory.map { PerformanceList.from(it) }
            return PerformanceListResponse(
                totalElements = totalElements,
                performanceList = performanceList
            )
        }
    }
}