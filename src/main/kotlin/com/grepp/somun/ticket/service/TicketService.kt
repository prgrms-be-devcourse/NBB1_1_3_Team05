package com.grepp.somun.ticket.service

import com.grepp.somun.ticket.dto.request.TicketRequestDto
import com.grepp.somun.ticket.dto.response.TicketResponseDto

/**
 * 티켓 서비스 인터페이스
 *
 * @author ycjung
 */
interface TicketService {
    // 전체 조회
    fun getAllTicketsByEmailWithPageAndSortOption(
        email: String,
        page: Int,
        size: Int,
        sortOption: String,
        isAscending: Boolean
    ): List<TicketResponseDto>

    // 상세 조회
    fun getTicketByEmailAndTicketId(email: String, ticketId: Long): TicketResponseDto

    // 등록
    fun registerTicket(email: String, ticketRequest: TicketRequestDto): TicketResponseDto

    // 삭제
    fun deleteTicket(email: String, ticketId: Long)
}