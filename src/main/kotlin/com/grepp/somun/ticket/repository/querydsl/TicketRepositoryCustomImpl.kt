package com.grepp.somun.ticket.repository.querydsl

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.performance.entity.QPerformanceEntity
import com.grepp.somun.ticket.entity.QTicketEntity
import com.grepp.somun.ticket.entity.TicketEntity
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.*

/**
 * QueryDSL 을 사용하기 위한 repo impl
 *
 * @author ycjung
 */
class TicketRepositoryCustomImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : TicketRepositoryCustom {

    /**
     * 이메일을 기반으로 멤버의 티켓 목록을 페이징 처리하여 정렬 옵션에 따라 반환합니다.
     *
     * @param email 티켓을 조회할 멤버의 이메일
     * @param pageable 페이징 구현체 전달
     * @return 멤버의 티켓 목록을 나타내는 TicketEntity 리스트
     */
    override fun getAllTicketsByEmailWithPageAndSortOption(email: String, pageable: Pageable): List<TicketEntity> {
        val ticketEntity = QTicketEntity.ticketEntity
        val orderSpecifiers = getOrderSpecifiers(pageable.sort, ticketEntity)

        return jpaQueryFactory.selectFrom(ticketEntity)
            .join(ticketEntity.performance, QPerformanceEntity.performanceEntity).fetchJoin()
            .where(ticketEntity.member.email.eq(email))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(*orderSpecifiers.toTypedArray())
            .fetch()
    }

    override fun getTicketByEmailAndTicketId(email: String, ticketId: Long): Optional<TicketEntity> {
        val ticketEntity = QTicketEntity.ticketEntity

        val ticket = jpaQueryFactory.selectFrom(ticketEntity)
            .join(ticketEntity.performance, QPerformanceEntity.performanceEntity).fetchJoin()
            .where(
                ticketEntity.ticketId.eq(ticketId)
                    // 임시로 member email을 통해 where 절 처리
                    .and(ticketEntity.member.email.eq(email))
            )
            .fetchOne()

        return Optional.ofNullable(ticket)
    }

    /**
     * Sort 객체로부터 OrderSpecifiers 목록을 생성합니다.
     *
     * @param sort Pageable에서 전달된 Sort 객체
     * @param ticketEntity 정렬 대상이 되는 QTicketEntity 객체
     * @return OrderSpecifier 목록
     * @throws GeneralException 잘못된 sortOption(property)가 제공된 경우 발생
     */
    private fun getOrderSpecifiers(sort: Sort, ticketEntity: QTicketEntity): List<OrderSpecifier<*>> {
        val orderSpecifiers = mutableListOf<OrderSpecifier<*>>()

        sort.forEach { order ->
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            val property = order.property

            // 각 필드에 맞게 OrderSpecifier 추가
            when (property) {
                "ticketId" -> orderSpecifiers.add(OrderSpecifier(direction, ticketEntity.ticketId))
                "price" -> orderSpecifiers.add(OrderSpecifier(direction, ticketEntity.price))
                "expired" -> orderSpecifiers.add(OrderSpecifier(direction, ticketEntity.deletedAt))
                else -> throw GeneralException(ErrorStatus._TICKET_INVALID_SORT_OPTION)
            }
        }

        return orderSpecifiers
    }
}