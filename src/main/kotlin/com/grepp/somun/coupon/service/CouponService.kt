package com.grepp.somun.coupon.service

import com.grepp.somun.coupon.dto.response.CouponResponseDto

/**
 * 쿠폰 서비스 인터페이스
 *
 * @author ycjung
 */
interface CouponService {
    fun getAllCouponsByMemberEmail(email: String, performanceId: Long?): List<CouponResponseDto>
    fun getFirstComeCoupon(email: String, performanceId: Long?): CouponResponseDto
}
