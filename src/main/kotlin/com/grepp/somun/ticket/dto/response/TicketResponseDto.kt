package com.grepp.somun.ticket.dto.response

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.ticket.entity.TicketEntity
import java.time.LocalDateTime

/**
 * Ticket 에 대한 Response 정보 매핑
 *
 * @author ycjung
 */
data class TicketResponseDto(
    val ticketId: Long,
    val performanceId: Long, // 공연 ID 추가
    val performanceTitle: String,
    val dateTime: LocalDateTime, // 티켓 예매 시간
    val quantity: Int, // 예매 인원
    val price: Int, // 티켓 총 가격
    val dateStartTime: LocalDateTime,
    val dateEndTime: LocalDateTime
) {
    companion object {
        // 정적 팩토리 메서드 create
        fun create(
            ticketId: Long,
            performanceId: Long,
            performanceTitle: String,
            dateTime: LocalDateTime,
            quantity: Int,
            price: Int,
            dateStartTime: LocalDateTime,
            dateEndTime: LocalDateTime
        ): TicketResponseDto {
            return TicketResponseDto(
                ticketId = ticketId,
                performanceId = performanceId,
                performanceTitle = performanceTitle,
                dateTime = dateTime,
                quantity = quantity,
                price = price,
                dateStartTime = dateStartTime,
                dateEndTime = dateEndTime
            )
        }

        // 엔티티로부터 DTO 생성하는 메서드 from
        fun fromEntity(ticketEntity: TicketEntity): TicketResponseDto {
            return TicketResponseDto(
                ticketId = ticketEntity.ticketId ?: throw GeneralException(ErrorStatus._TICKET_NOT_FOUND),
                performanceId = ticketEntity.performance.performanceId
                    ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND),
                performanceTitle = ticketEntity.performance.title,
                dateTime = ticketEntity.dateTime,
                quantity = ticketEntity.quantity,
                price = ticketEntity.price,
                dateStartTime = ticketEntity.createdAt ?: LocalDateTime.MIN, // null 안전 처리
                dateEndTime = ticketEntity.deletedAt ?: LocalDateTime.MIN // null 안전 처리
            )
        }
    }
}