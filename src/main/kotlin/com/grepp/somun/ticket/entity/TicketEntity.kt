package com.grepp.somun.ticket.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.performance.entity.PerformanceEntity
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 티켓 엔티티
 *
 * @author ycjung
 */
@Entity
@Table(name = "ticket")
data class TicketEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id", nullable = false)
    val ticketId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    val performance: PerformanceEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @Column(name = "date_time", nullable = false)
    val dateTime: LocalDateTime,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "price", nullable = false)
    val price: Int
) : BaseEntity()