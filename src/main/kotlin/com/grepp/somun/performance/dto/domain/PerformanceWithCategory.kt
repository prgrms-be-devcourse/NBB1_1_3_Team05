package com.grepp.somun.performance.dto.domain

import com.grepp.somun.performance.entity.PerformanceStatus
import java.time.LocalDateTime

/**
 * query dsl로 모든 공연 정보를 받는 dto
 */
data class PerformanceWithCategory(
    val memberName: String,
    val performanceId: Long,
    val title: String,
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime,
    val address: String,
    val imageUrl: String?,
    val price: Int,
    val status: PerformanceStatus,
    val remainingTicket: Int?,
    var categories: List<CategoryContent>? = null
) {
    constructor(
        memberName: String,
        performanceId: Long,
        title: String,
        dateStartTime: LocalDateTime,
        dateEndTime: LocalDateTime,
        address: String,
        imageUrl: String?,
        price: java.lang.Integer,
        status: PerformanceStatus,
        remainingTicket: java.lang.Integer?
    ) : this(
        memberName,
        performanceId,
        title,
        dateStartTime,
        dateEndTime,
        address,
        imageUrl,
        price.toInt(),
        status,
        remainingTicket?.toInt(),
        null
    )
    fun updateCategories(newCategories: List<CategoryContent>) {
        categories = newCategories
    }
}
