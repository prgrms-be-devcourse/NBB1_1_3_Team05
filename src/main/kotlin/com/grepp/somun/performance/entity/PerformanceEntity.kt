package com.grepp.somun.performance.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import lombok.AccessLevel
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder

@jakarta.persistence.Entity(name = "performance")
@lombok.Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class PerformanceEntity : BaseEntity() {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "performance_id")
    private var performanceId: Long? = null

    @jakarta.persistence.ManyToOne
    @JoinColumn(name = "member_id")
    private var member: MemberEntity? = null

    @jakarta.persistence.Column(name = "title", nullable = false, length = 50)
    private var title: String? = null

    @jakarta.persistence.Column(name = "date_start_time")
    private var dateStartTime: java.time.LocalDateTime? = null

    @jakarta.persistence.Column(name = "date_end_time")
    private var dateEndTime: java.time.LocalDateTime? = null

    @jakarta.persistence.Column(name = "description")
    private var description: String? = null

    @jakarta.persistence.Column(name = "max_audience")
    private var maxAudience: Int? = null

    @jakarta.persistence.Column(name = "address")
    private var address: String? = null

    @jakarta.persistence.Column(name = "image_url")
    private var imageUrl: String? = null

    @jakarta.persistence.Column(name = "price")
    private var price: Int? = null

    @jakarta.persistence.Column(name = "remaining_tickets")
    private var remainingTickets = 0

    @jakarta.persistence.Column(name = "start_date")
    private var startDate: java.time.LocalDateTime? = null

    @jakarta.persistence.Column(name = "status")
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private var performanceStatus: PerformanceStatus? = null

    @jakarta.persistence.OneToMany(
        mappedBy = "performance",
        cascade = [jakarta.persistence.CascadeType.ALL],
        orphanRemoval = true
    )
    @lombok.Builder.Default
    private val performanceCategoryList: List<PerformanceCategoryEntity> = java.util.ArrayList()

    fun updatePerformance(performanceEntity: PerformanceEntity) {
        if (performanceEntity.title != null) {
            this.title = performanceEntity.title
        }
        if (performanceEntity.dateStartTime != null) {
            this.dateStartTime = performanceEntity.dateStartTime
        }
        if (performanceEntity.dateEndTime != null) {
            this.dateEndTime = performanceEntity.dateEndTime
        }
        if (performanceEntity.description != null) {
            this.description = performanceEntity.description
        }
        if (performanceEntity.address != null) {
            this.address = performanceEntity.address
        }
        if (performanceEntity.imageUrl != null) {
            this.imageUrl = performanceEntity.imageUrl
        }
        if (performanceEntity.price != null) {
            this.price = performanceEntity.price
        }
        if (performanceEntity.maxAudience != null) {
            this.maxAudience = performanceEntity.maxAudience
        }
        if (performanceEntity.performanceStatus != null) {
            this.performanceStatus = performanceEntity.performanceStatus
        }
    }

    fun updateMember(memberEntity: MemberEntity?) {
        this.member = memberEntity
    }

    fun updateDeleteAt() {
        this.recordDeletedAt(java.time.LocalDateTime.now())
    }

    fun updateImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun updateTicket(remainingTickets: Int) {
        this.remainingTickets = remainingTickets
    }
}
