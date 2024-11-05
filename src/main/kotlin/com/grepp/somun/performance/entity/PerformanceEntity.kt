package com.grepp.somun.performance.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import jakarta.persistence.*
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity(name = "performance")
class PerformanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    var performanceId: Long? = null,

    @ManyToOne
    @JoinColumn(name = "member_id")
    var member: MemberEntity? = null,

    @Column(name = "title", nullable = false, length = 50)
    var title: String,

    @Column(name = "date_start_time", nullable = false)
    var dateStartTime: LocalDateTime,

    @Column(name = "date_end_time", nullable = false)
    var dateEndTime: LocalDateTime,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "max_audience")
    var maxAudience: Int? = null,

    @Column(name = "address", nullable = false)
    var address: String,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "start_date")
    var startDate: LocalDateTime? = null,

    @Column(name = "coordinate", columnDefinition = "GEOMETRY(POINT, 4326)")
    var coordinate: Point,

    @Column(name = "remaining_tickets")
    var remainingTickets: Int?,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var performanceStatus: PerformanceStatus? = null,

    @OneToMany(
        mappedBy = "performance",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var performanceCategoryList: MutableList<PerformanceCategoryEntity> = mutableListOf()
) : BaseEntity() {

    fun updatePerformance(update: PerformanceEntity) {
        update.title?.let { this.title = it }
        update.dateStartTime?.let { this.dateStartTime = it }
        update.dateEndTime?.let { this.dateEndTime = it }
        update.description?.let { this.description = it }
        update.address?.let { this.address = it }
        update.imageUrl?.let { this.imageUrl = it }
        update.price?.let { this.price = it }
        update.maxAudience?.let { this.maxAudience = it }
        update.performanceStatus?.let { this.performanceStatus = it }
    }

    fun updateMember(member: MemberEntity) {
        this.member = member
    }

    fun updateDeleteAt() {
        recordDeletedAt(LocalDateTime.now())
    }

    fun updateImageUrl(imageUrl: String?) {
        this.imageUrl = imageUrl
    }

    fun updateTicket(remainingTickets: Int) {
        this.remainingTickets = remainingTickets
    }
}