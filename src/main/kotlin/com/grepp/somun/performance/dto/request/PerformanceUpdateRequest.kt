package com.grepp.somun.performance.dto.request

import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

data class PerformanceUpdateRequest(
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val address: String,
    val imageUrl: String,
    val price: Int,
    val description: String,
    val remainTickets: Int,
    val maxAudience: Int,
    val status: PerformanceStatus
) {
    fun toEntity(): PerformanceEntity {
        return PerformanceEntity(
            title = title,
            dateStartTime = dateStartTime,
            dateEndTime = dateEndTime,
            address = address,
            imageUrl = imageUrl,
            price = price,
            remainingTickets = remainTickets,
            description = description,
            maxAudience = maxAudience,
            status = status
        )
    }
}