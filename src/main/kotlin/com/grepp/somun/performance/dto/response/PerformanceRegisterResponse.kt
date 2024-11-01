package com.grepp.somun.performance.dto.response

import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

data class PerformanceRegisterResponse(
    val memberName: String,
    val performanceId: Long,
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val description: String,
    val maxAudience: Int,
    val address: String,
    val imageUrl: String?,
    val price: Int,
    val remainingTickets: Int?,
    val startDate: LocalDateTime,
    val status: PerformanceStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val categories: List<CategoryDto>?
) {
    companion object {
        fun of(performanceEntity: PerformanceEntity, categories: List<CategoryDto>): PerformanceRegisterResponse {
            return PerformanceRegisterResponse(
                memberName = performanceEntity.member!!.name,
                performanceId = performanceEntity.performanceId!!,
                title = performanceEntity.title!!,
                dateStartTime = performanceEntity.dateStartTime!!,
                dateEndTime = performanceEntity.dateEndTime!!,
                description = performanceEntity.description!!,
                maxAudience = performanceEntity.maxAudience!!,
                address = performanceEntity.address!!,
                imageUrl = performanceEntity.imageUrl,
                price = performanceEntity.price!!,
                remainingTickets = performanceEntity.remainingTickets,
                startDate = performanceEntity.startDate!!,
                status = performanceEntity.performanceStatus!!,
                createdAt = performanceEntity.createdAt!!,
                updatedAt = performanceEntity.updatedAt!!,
                categories = categories
            )
        }
    }
}