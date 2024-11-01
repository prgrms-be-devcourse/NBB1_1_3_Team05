package com.grepp.somun.coupon.repository.querydsl

import com.grepp.somun.coupon.entity.CouponEntity
import com.grepp.somun.coupon.entity.QCouponEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository
import java.util.*

/**
 * QueryDSL을 사용하기 위한 repo impl
 *
 * @author ycjung
 */
@Repository
class CouponRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : CouponRepositoryCustom {

    override fun getAllCouponsByMemberEmail(email: String, performanceId: Long?): List<CouponEntity> {
        val couponEntity: QCouponEntity = QCouponEntity.couponEntity
        return jpaQueryFactory.selectFrom(couponEntity)
            .where(
                couponEntity.member.email.eq(email)
                    .and(
                        couponEntity.performance.performanceId.eq(performanceId)
                            .or(couponEntity.performance.isNull)
                    )
            )
            .fetch()
    }

    override fun getFirstComeCouponByPerformanceId(performanceId: Long?): Optional<CouponEntity> {
        val couponEntity: QCouponEntity = QCouponEntity.couponEntity
        val coupon: CouponEntity? = jpaQueryFactory.selectFrom(couponEntity)
            .where(
                couponEntity.performance.performanceId.eq(performanceId)
                    .and(couponEntity.member.isNull)
            ) // member_id가 null
            .orderBy(couponEntity.couponId.asc()) // coupon_id로 오름차순 정렬
            .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 비관적 Lock 적용
            .limit(1)
            .fetchOne()
        return Optional.ofNullable(coupon)
    }

    override fun getCouponByPerformanceIdAndMemberId(performanceId: Long?, memberId: Long?): Optional<CouponEntity> {
        val couponEntity: QCouponEntity = QCouponEntity.couponEntity
        val coupon: CouponEntity? = jpaQueryFactory.selectFrom(couponEntity)
            .where(
                couponEntity.performance.performanceId.eq(performanceId)
                    .and(couponEntity.member.memberId.eq(memberId))
            ) // member_id가 지정된 값과 일치
            .fetchOne()
        return Optional.ofNullable(coupon)
    }
}