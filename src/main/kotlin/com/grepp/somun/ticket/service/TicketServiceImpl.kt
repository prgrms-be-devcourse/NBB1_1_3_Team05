package com.grepp.somun.ticket.service

import com.grepp.somun.coupon.entity.CouponEntity
import com.grepp.somun.coupon.repository.CouponRepository
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.repository.PerformanceRepository
import com.grepp.somun.ticket.dto.request.TicketRequestDto
import com.grepp.somun.ticket.dto.response.TicketResponseDto
import com.grepp.somun.ticket.entity.TicketEntity
import com.grepp.somun.ticket.repository.TicketRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * 티켓 서비스 구현체
 *
 * @author ycjung
 */
@Service
class TicketServiceImpl(
    private val memberRepository: MemberRepository,
    private val performanceRepository: PerformanceRepository,
    private val couponRepository: CouponRepository,
    private val ticketRepository: TicketRepository
) : TicketService {

    // 내부에서 사용 - 회원 정보 Entity 가져오기(by email)
    private fun findMemberByEmail(email: String): MemberEntity {
        return memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus._UNAUTHORIZED) }
    }

    // 내부에서 사용 - 공연 정보 Entity 가져오기(by performanceId)
    private fun findPerformanceById(performanceId: Long): PerformanceEntity {
        return performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }
    }

    /**
     * 티켓 정보 전체 조회(회원 체크, 페이징, Sort-ASC|DESC)
     */
    override fun getAllTicketsByEmailWithPageAndSortOption(
        email: String,
        page: Int,
        size: Int,
        sortOption: String,
        isAscending: Boolean
    ): List<TicketResponseDto> {
        val sort = if (isAscending) Sort.by(sortOption).ascending() else Sort.by(sortOption).descending()
        val pageable: Pageable = PageRequest.of(page, size, sort)

        return ticketRepository.getAllTicketsByEmailWithPageAndSortOption(email, pageable)
            .stream()
            .map { TicketResponseDto.fromEntity(it) }
            .toList()
    }

    /**
     * 티켓 상세 조회
     */
    override fun getTicketByEmailAndTicketId(email: String, ticketId: Long): TicketResponseDto {
        val ticketEntity = ticketRepository.getTicketByEmailAndTicketId(email, ticketId)
            .orElseThrow { GeneralException(ErrorStatus._TICKET_NOT_FOUND) }

        return TicketResponseDto.fromEntity(ticketEntity)
    }

    /**
     * 티켓 발권
     */
    @Transactional
    override fun registerTicket(email: String, ticketRequest: TicketRequestDto): TicketResponseDto {
        val memberEntity = findMemberByEmail(email)
        val performanceEntity = findPerformanceById(ticketRequest.performanceId)
        val finalPrice = calculateFinalPrice(performanceEntity.price, ticketRequest.quantity, ticketRequest.couponId)

        val ticketEntity = createAndSaveTicket(memberEntity, performanceEntity, ticketRequest.quantity, finalPrice)
        return TicketResponseDto.fromEntity(ticketEntity)
    }

    /**
     * 티켓 취소
     */
    override fun deleteTicket(email: String, ticketId: Long) {
        val ticketEntity = ticketRepository.findById(ticketId)
            .orElseThrow { GeneralException(ErrorStatus._TICKET_NOT_FOUND) }

        if (ticketEntity.member.email != email) {
            throw GeneralException(ErrorStatus._FORBIDDEN) // 권한 없음 예외
        }

        ticketRepository.deleteById(ticketId)
        // TODO: 티켓 취소 시에 환불 절차 필요.
    }

    private fun calculateFinalPrice(performancePrice: Int, quantity: Int, couponId: Long?): Int {
        var discountPercent = 0
        var finalPrice = performancePrice * quantity

        couponId?.let {
            val couponEntity = findAndValidateCoupon(it)
            discountPercent = couponEntity.percent
            finalPrice = calculateDiscountedPrice(performancePrice, quantity, discountPercent)

            couponEntity.isUsed = true
            couponRepository.save(couponEntity)
        }

        return finalPrice
    }

    fun calculateDiscountedPrice(performancePrice: Int, quantity: Int, discountPercent: Int): Int {
        if (performancePrice < 0 || quantity < 0 || discountPercent < 0) {
            throw GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR)
        }

        val totalPrice = performancePrice * quantity
        val discountAmount = (totalPrice * discountPercent) / 100
        return totalPrice - discountAmount
    }

    private fun findAndValidateCoupon(couponId: Long): CouponEntity {
        val couponEntity = couponRepository.findById(couponId)
            .orElseThrow { GeneralException(ErrorStatus._COUPON_NOT_FOUND) }

        if (couponEntity!!.isUsed) {
            throw GeneralException(ErrorStatus._COUPON_ALREADY_USED)
        }

        if (couponEntity.expireTime!!.isBefore(LocalDateTime.now())) {
            throw GeneralException(ErrorStatus._COUPON_EXPIRED)
        }

        return couponEntity
    }

    private fun createAndSaveTicket(
        memberEntity: MemberEntity,
        performanceEntity: PerformanceEntity,
        quantity: Int,
        finalPrice: Int
    ): TicketEntity {
        val ticketEntity = TicketEntity(
            member = memberEntity,
            performance = performanceEntity,
            dateTime = LocalDateTime.now(),
            quantity = quantity,
            price = finalPrice
        )
        return ticketRepository.save(ticketEntity)
    }
}