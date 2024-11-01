package com.grepp.somun.performance.dto.domain

import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

data class PerformanceDetail(
    val memberName: String? = null,
    val performanceId: Long? = null,
    val title: String? = null,
    val dateStartTime: LocalDateTime? = null,
    val dateEndTime: LocalDateTime? = null,
    val description: String? = null,
    val maxAudience: Int? = null,
    val address: String? = null,
    val imageUrl: String? = null,
    val price: Int? = null,
    val remainingTickets: Int? = null,
    val startDate: LocalDateTime? = null,
    val status: PerformanceStatus? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    var categories: List<CategoryContent>? = null
) {
    fun updateCategories(newCategories: List<CategoryContent>?) {
        categories = newCategories
    }
}
