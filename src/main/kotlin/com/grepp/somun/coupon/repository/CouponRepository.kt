package com.grepp.somun.coupon.repository

import com.grepp.somun.coupon.entity.CouponEntity
import com.grepp.somun.coupon.repository.querydsl.CouponRepositoryCustom
import com.grepp.somun.performance.entity.PerformanceEntity
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 쿠폰 레파지토리
 *
 * @author ycjung
 */
interface CouponRepository : JpaRepository<CouponEntity?, Long?>, CouponRepositoryCustom {
    fun findByPerformance_PerformanceId(performanceId: Long?): List<CouponEntity>
    fun findByPerformance(performanceEntity: PerformanceEntity): List<CouponEntity>
}
