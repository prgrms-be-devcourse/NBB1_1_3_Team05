package com.grepp.somun.performance.dto.request

import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

data class PerformanceRegisterRequest(
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val address: String,
    val imageUrl: String,
    val price: Int,
    val description: String,
    val maxAudience: Int,
    val startDate: LocalDateTime,
    val categories: List<Long>
) {
    fun toEntity(): PerformanceEntity {
        return PerformanceEntity(
            title = title,
            dateStartTime = dateStartTime,
            dateEndTime = dateEndTime,
            description = description,
            maxAudience = maxAudience,
            address = address,
            imageUrl = imageUrl,
            price = price,
            remainingTickets = maxAudience,
            startDate = startDate,
            performanceStatus = PerformanceStatus.NOT_CONFIRMED
        )
    }
}