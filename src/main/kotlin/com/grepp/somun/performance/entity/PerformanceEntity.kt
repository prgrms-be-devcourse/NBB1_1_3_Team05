package com.grepp.somun.performance.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "performance")
@Table(name = "performance")
class PerformanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    var performanceId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @Column(name = "title", nullable = false, length = 50)
    var title: String? = null,

    @Column(name = "date_start_time")
    var dateStartTime: LocalDateTime? = null,

    @Column(name = "date_end_time")
    var dateEndTime: LocalDateTime? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "max_audience")
    var maxAudience: Int? = null,

    @Column(name = "address")
    var address: String? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "price")
    var price: Int? = null,

    @Column(name = "remaining_tickets")
    var remainingTickets: Int = 0,

    @Column(name = "start_date")
    var startDate: LocalDateTime? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var performanceStatus: PerformanceStatus? = null,

) : BaseEntity() {
    @jakarta.persistence.OneToMany(
        mappedBy = "performance",
        cascade = [jakarta.persistence.CascadeType.ALL],
        orphanRemoval = true
    )
    @lombok.Builder.Default
    private val performanceCategoryList: List<PerformanceCategoryEntity> = java.util.ArrayList()

    fun updatePerformance(performanceEntity: PerformanceEntity) {
        performanceEntity.title?.let { this.title = it }
        performanceEntity.dateStartTime?.let { this.dateStartTime = it }
        performanceEntity.dateEndTime?.let { this.dateEndTime = it }
        performanceEntity.description?.let { this.description = it }
        performanceEntity.address?.let { this.address = it }
        performanceEntity.imageUrl?.let { this.imageUrl = it }
        performanceEntity.price?.let { this.price = it }
        performanceEntity.maxAudience?.let { this.maxAudience = it }
        performanceEntity.performanceStatus?.let { this.performanceStatus = it }
    }

    fun updateMember(memberEntity: MemberEntity?) {
        this.member = memberEntity
    }

    fun updateDeleteAt() {
        this.recordDeletedAt(LocalDateTime.now())
    }

    fun updateImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun updateTicket(remainingTickets: Int) {
        this.remainingTickets = remainingTickets
    }
}
