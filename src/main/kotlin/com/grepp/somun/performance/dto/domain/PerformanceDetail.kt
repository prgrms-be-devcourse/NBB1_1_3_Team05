package com.grepp.somun.performance.dto.domain

import com.grepp.somun.coupon.dto.response.CouponResponseDto
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
    var categories: List<CategoryContent>? = null,
    var firstComeCoupons: List<CouponResponseDto>? = null
) {
    constructor(
        memberName: String,
        performanceId: Long,
        title: String,
        dateStartTime: LocalDateTime,
        dateEndTime: LocalDateTime,
        description: String,
        maxAudience: Int,
        address: String,
        imageUrl: String?,
        price: Int,
        remainingTickets: Int?,
        startDate: LocalDateTime?,
        status: PerformanceStatus,
        createdAt: LocalDateTime,
        updatedAt: LocalDateTime
    ) : this(
        memberName,
        performanceId,
        title,
        dateStartTime,
        dateEndTime,
        description,
        maxAudience,
        address,
        imageUrl,
        price,
        remainingTickets,
        startDate,
        status,
        createdAt,
        updatedAt,
        null
    )
    fun updateCategories(newCategories: List<CategoryContent>?) {
        categories = newCategories
    }

    fun updateFirstComeCoupons(firstComeCoupons: List<CouponResponseDto>) {
        this.firstComeCoupons = firstComeCoupons
    }
}
