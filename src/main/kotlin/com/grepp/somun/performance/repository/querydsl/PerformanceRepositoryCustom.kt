package com.grepp.somun.performance.repository.querydsl

import com.grepp.somun.performance.dto.domain.PerformanceDetail
import com.grepp.somun.performance.dto.domain.PerformanceWithCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface PerformanceRepositoryCustom {
    fun getPerformanceWithCategoryList(
        pageable: Pageable,
        categoryId: Long?,
        search: String?,
        email: String?
    ): Page<PerformanceWithCategory>

    // 사용자 선호 카테고리 공연 추천 조회
    fun getRecommendedPerformancesByMember(memberId: Long): List<PerformanceWithCategory>

    // 실시간 인기 공연 조회
    fun getPerformancesByIds(performanceIds: List<Long>): List<PerformanceWithCategory>

    fun getPerformanceDetail(performanceId: Long): PerformanceDetail?

    fun getMyPerformanceWithCategoryList(email: String, pageable: Pageable): Page<PerformanceWithCategory>
}
