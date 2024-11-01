package com.grepp.somun.coupon.repository.querydsl

import com.grepp.somun.coupon.entity.CouponEntity
import java.util.*

/**
 * QueryDSL 을 사용하기 위한 repo
 *
 * @author ycjung
 */
interface CouponRepositoryCustom {
    fun getAllCouponsByMemberEmail(email: String, performanceId: Long?): List<CouponEntity>
    fun getFirstComeCouponByPerformanceId(performanceId: Long?): Optional<CouponEntity>
    fun getCouponByPerformanceIdAndMemberId(performanceId: Long?, memberId: Long): Optional<CouponEntity>
}
