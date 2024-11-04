package com.grepp.somun.ticket.controller

import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.ticket.dto.request.TicketRequestDto
import com.grepp.somun.ticket.dto.response.TicketResponseDto
import com.grepp.somun.ticket.service.TicketService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * 티켓 데이터 컨트롤러
 *
 * @author ycjung
 */
@RestController
@RequestMapping("/api/v1/tickets")
class TicketController(
    private val ticketService: TicketService
) {
    private val log = LoggerFactory.getLogger(TicketController::class.java)

    /**
     * 나의 티켓 전체 조회 - 페이징 처리
     */
    @GetMapping
    fun getAllTicketsByEmailWithPageAndSortOption(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int,
        @RequestParam(value = "option", defaultValue = "ticketId") option: String,
        @RequestParam(value = "isAscending", defaultValue = "false") isAscending: Boolean
    ): ResponseEntity<ApiResponse<List<TicketResponseDto>>> {
        log.info("Get tickets by member id with pagination and sort - Page: {}, Size: {}, Sort Option: {}, Ascending : {}", page, size, option, isAscending)

        if (page < 0 || size < 0) {
            throw GeneralException(ErrorStatus._TICKET_INVALID_PAGINATION_PARAMETERS)
        }

        return ApiResponse.onSuccess(ticketService.getAllTicketsByEmailWithPageAndSortOption(userDetails.username, page, size, option, isAscending))
    }

    /**
     * 나의 티켓 상세 조회
     */
    @GetMapping("/{ticketId}")
    fun getTicketById(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("ticketId") ticketId: Long?
    ): ResponseEntity<ApiResponse<TicketResponseDto>> {
        log.info("Get ticket detail by id: {}", ticketId)

        if (ticketId == null) {
            throw GeneralException(ErrorStatus._TICKET_ID_MISSING)
        }
        return ApiResponse.onSuccess(ticketService.getTicketByEmailAndTicketId(userDetails.username, ticketId))
    }

    /**
     * 티켓 발권
     */
    @PostMapping
    fun buyTicket(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody ticketRequestDto: TicketRequestDto
    ): ResponseEntity<ApiResponse<TicketResponseDto>> {
        log.info("Buy ticket: {}", ticketRequestDto)

        return ApiResponse.onSuccess(ticketService.registerTicket(userDetails.username, ticketRequestDto))
    }

    /**
     * 티켓 취소
     */
    @DeleteMapping("/{ticketId}")
    fun cancelTicket(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("ticketId") ticketId: Long
    ): ResponseEntity<ApiResponse<Void>> {
        log.info("Cancel ticket by id: {}", ticketId)

        ticketService.deleteTicket(userDetails.username, ticketId)

        return ApiResponse.onSuccess()
    }
}