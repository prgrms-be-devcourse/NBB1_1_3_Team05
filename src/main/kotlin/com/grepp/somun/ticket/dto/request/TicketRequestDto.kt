package com.grepp.somun.ticket.dto.request

/**
 * Ticket 에 대한 Request 정보 매핑
 *
 * @author ycjung
 */
data class TicketRequestDto(
    val performanceId: Long,
    val quantity: Int,
    val couponId: Long?
) {
    companion object {
        // 정적 팩토리 메서드 create
        fun create(performanceId: Long, quantity: Int, couponId: Long?): TicketRequestDto {
            return TicketRequestDto(
                performanceId = performanceId,
                quantity = quantity,
                couponId = couponId
            )
        }
    }
}