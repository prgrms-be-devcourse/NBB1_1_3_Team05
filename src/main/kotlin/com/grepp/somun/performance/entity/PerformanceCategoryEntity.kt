package com.grepp.somun.performance.entity

import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDateTime

@Entity(name = "performance_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class PerformanceCategoryEntity private constructor(
    performance: PerformanceEntity,
    @ManyToOne @JoinColumn(name = "category_id") private val category: CategoryEntity,
    @Column(name = "created_at") private var createdAt: LocalDateTime,
    @Column(name = "updated_at") private var updatedAt: LocalDateTime
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_category_id")
    private var performanceCategoryId: Long? = null

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private val performance: PerformanceEntity = performance

    companion object {
        fun of(performanceEntity: PerformanceEntity, categoryEntity: CategoryEntity): PerformanceCategoryEntity {
            return PerformanceCategoryEntity(
                performanceEntity,
                categoryEntity,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        }
    }
}
