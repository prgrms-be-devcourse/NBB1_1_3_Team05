package com.grepp.somun.coupon.dto.response

import com.grepp.somun.coupon.entity.CouponEntity
import lombok.Builder
import java.time.LocalDateTime

/**
 * Coupon 에 대한 Response 정보 매핑
 *
 * @author ycjung
 */
@Builder
@JvmRecord
data class CouponResponseDto(
    val couponId: Long,
    val name: String,
    val percent: Int,
    val isUsed: Boolean,
    val expireTime: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val performanceTitle: String? = null
) {
    companion object {
        // 정적 팩토리 메서드 create
        fun create(
            couponId: Long, name: String, percent: Int, isUsed: Boolean,
            expireTime: LocalDateTime, createdAt: LocalDateTime
        ): CouponResponseDto {
            return CouponResponseDto(
                couponId = couponId,
                name = name,
                percent = percent,
                isUsed = isUsed,
                expireTime = expireTime,
                createdAt = createdAt
            )
        }

        // 엔티티로부터 DTO를 생성하는 메서드 from
        fun fromEntity(couponEntity: CouponEntity): CouponResponseDto {
            return CouponResponseDto(
                couponId = couponEntity.couponId,
                name = couponEntity.name,
                percent = couponEntity.percent,
                isUsed = couponEntity.isUsed,
                expireTime = couponEntity.expireTime,
                createdAt = couponEntity.createdAt,
                performanceTitle = couponEntity.performance?.title
            )
        }
    }
}
