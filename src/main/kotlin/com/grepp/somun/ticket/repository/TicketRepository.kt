package com.grepp.somun.ticket.repository

import com.grepp.somun.ticket.entity.TicketEntity
import com.grepp.somun.ticket.repository.querydsl.TicketRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 티켓 레파지토리
 *
 * @author ycjung
 */
interface TicketRepository :  JpaRepository<TicketEntity, Long>, TicketRepositoryCustom