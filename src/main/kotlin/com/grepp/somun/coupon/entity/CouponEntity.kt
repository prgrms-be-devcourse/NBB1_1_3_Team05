package com.grepp.somun.coupon.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.performance.entity.PerformanceEntity
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 쿠폰 엔티티
 *
 * @author ycjung
 */
@Entity
@Table(name = "coupon")
class CouponEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    val couponId: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    val performance: PerformanceEntity? = null,

    @Column(name = "name", nullable = false, length = 30)
    val name: String,

    @Column(name = "percent", nullable = false)
    val percent: Int,

    @Column(name = "is_used", nullable = false)
    var isUsed: Boolean,

    @Column(name = "expire_time")
    var expireTime: LocalDateTime? = null
) : BaseEntity() {

//    fun setUsed(used: Boolean) {
//        isUsed = used
//    }

    fun updateMemberAndExpiration(member: MemberEntity?, expireAfterDays: Long = 3) {
        this.member = member
        expireTime = LocalDateTime.now().plusDays(expireAfterDays)
    }
}