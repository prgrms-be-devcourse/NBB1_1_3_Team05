package com.grepp.somun.coupon.service

import com.grepp.somun.coupon.dto.response.CouponResponseDto
import com.grepp.somun.coupon.entity.CouponEntity
import com.grepp.somun.coupon.repository.CouponRepository
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 쿠폰 서비스 구현체
 *
 * @author ycjung
 */
@Service
class CouponServiceImpl(
    private val couponRepository: CouponRepository,
    private val memberRepository: MemberRepository
) : CouponService {

    override fun getAllCouponsByMemberEmail(email: String, performanceId: Long?): List<CouponResponseDto> {
        return couponRepository.getAllCouponsByMemberEmail(email, performanceId)
            .map(CouponResponseDto::fromEntity)
    }

    @Transactional
    override fun getFirstComeCoupon(email: String, performanceId: Long?): CouponResponseDto {
        // 멤버 조회
        val member: MemberEntity = findMemberByEmail(email)

        // 선착순 티켓 발급 여부 검증
        validateAlreadyReceivedFirstComeCoupon(performanceId, member)
        val coupon: CouponEntity = couponRepository.getFirstComeCouponByPerformanceId(performanceId)
            .orElseThrow { GeneralException(ErrorStatus._FIRST_COME_COUPON_NOT_FOUND) }

        coupon.updateMemberAndExpiration(member)
        return CouponResponseDto.fromEntity(coupon)
    }

    private fun findMemberByEmail(email: String): MemberEntity {
        return memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
    }

    private fun validateAlreadyReceivedFirstComeCoupon(performanceId: Long?, member: MemberEntity) {
        if (couponRepository.getCouponByPerformanceIdAndMemberId(performanceId, member.memberId).isPresent) {
            throw GeneralException(ErrorStatus._ALREADY_RECEIVED_FIRST_COME_COUPON)
        }
    }
}