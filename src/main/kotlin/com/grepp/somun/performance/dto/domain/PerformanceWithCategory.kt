package com.grepp.somun.performance.dto.domain

import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

/**
 * query dsl로 모든 공연 정보를 받는 dto
 */
data class PerformanceWithCategory(
    val memberName: String? = null,
    val performanceId: Long? = null,
    val title: String? = null,
    val dateStartTime: LocalDateTime? = null,
    val dateEndTime: LocalDateTime? = null,
    val address: String? = null,
    val imageUrl: String? = null,
    val price: Int = 0,
    val status: PerformanceStatus? = null,
    val remainingTicket: Int? = null,
    var categories: List<CategoryContent>? = null
) {
    fun updateCategories(newCategories: List<CategoryContent>) {
        categories = newCategories
    }
}
