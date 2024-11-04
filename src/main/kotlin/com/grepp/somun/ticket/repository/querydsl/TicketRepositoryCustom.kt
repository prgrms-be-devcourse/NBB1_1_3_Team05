package com.grepp.somun.ticket.repository.querydsl

import com.grepp.somun.ticket.entity.TicketEntity
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * QueryDSL 을 사용하기 위한 repo
 *
 * @author ycjung
 */
interface TicketRepositoryCustom {

    fun getAllTicketsByEmailWithPageAndSortOption(email: String, pageable: Pageable): List<TicketEntity>
    fun getTicketByEmailAndTicketId(email: String, ticketId: Long): Optional<TicketEntity>
}