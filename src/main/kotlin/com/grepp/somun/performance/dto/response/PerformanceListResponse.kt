package com.grepp.somun.performance.dto.response

import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.domain.PerformanceWithCategory
import java.time.LocalDateTime

data class PerformanceListResponse(
    val totalElements: Long = 0,
    val performanceList: List<PerformanceList> = emptyList()
) {
    data class PerformanceList(
        val memberName: String = "",
        val performanceId: Long = 0L,
        val title: String = "",
        val dateStartTime: LocalDateTime = LocalDateTime.MIN,
        val dateEndTime: LocalDateTime = LocalDateTime.MIN,
        val address: String = "",
        val imageUrl: String? = null,
        val price: Int = 0,
        val status: String = "",
        val remainingTicket: Int? = null,
        val categories: List<CategoryDto>? = null
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
                    price = performanceWithCategory.price!!,
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