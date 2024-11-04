package com.grepp.somun.performance.service

import com.grepp.somun.performance.dto.response.PerformanceListResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PerformanceViewCountServiceImpl(
    @Qualifier("longRedisTemplate") private val longRedisTemplate: RedisTemplate<String, Long>,
    @Qualifier("performanceRedisTemplate") private val performanceListResponseRedisTemplate: RedisTemplate<String, PerformanceListResponse>,
    private val performanceService: PerformanceService
) : PerformanceViewCountService {

    companion object {
        private const val RECENT_VIEW_COUNT_KEY = "recent_view_count"
        private const val PERFORMANCE_VIEW_COUNT_KEY = "performance_view_count"
        private const val PERFORMANCE_VIEW_CACHE_KEY = "popular_performances_cache"
        private const val MAX_RECENT_VIEWS = 200L
        private const val SCHEDULER_TIME = 300_000L  // 5분 (300,000ms)
    }

    private val log = LoggerFactory.getLogger(PerformanceViewCountServiceImpl::class.java)

    /**
     * 사용자가 특정 공연 조회 시 Redis에 조회수 증감
     * @param performanceId
     */
    override fun incrementViewCount(performanceId: Long) {
        longRedisTemplate.opsForList().leftPush(RECENT_VIEW_COUNT_KEY, performanceId)
        longRedisTemplate.opsForZSet().incrementScore(PERFORMANCE_VIEW_COUNT_KEY, performanceId, 1.0)

        val listSize = longRedisTemplate.opsForList().size(RECENT_VIEW_COUNT_KEY) ?: 0L
        if (listSize > MAX_RECENT_VIEWS) {
            val oldestPerformanceId = longRedisTemplate.opsForList().rightPop(RECENT_VIEW_COUNT_KEY)
            if (oldestPerformanceId != null) {
                longRedisTemplate.opsForZSet().incrementScore(PERFORMANCE_VIEW_COUNT_KEY, oldestPerformanceId, -1.0)
            }
        }
    }

    /**
     * 5분마다 실시간 인기 공연 갱신
     * 메인 DB에서 공연 정보 받아와서 Redis에 캐싱
     */
    @Scheduled(fixedRate = SCHEDULER_TIME)
    fun cacheTopPerformance() {
        log.info("실시간 인기 공연 스케줄러 동작")

        val performanceIds = getPopularPerformanceIds()
        if (performanceIds.isNotEmpty()) {
            val popularPerformances = performanceService.getPopularPerformances(performanceIds)
            performanceListResponseRedisTemplate.opsForValue().set(PERFORMANCE_VIEW_CACHE_KEY, popularPerformances)
        }
    }

    /**
     * Redis 캐시에서 실시간 인기 공연 조회
     * 캐시에 없다면 MySQL에서 직접 조회 후 캐싱
     * @return
     */
    override fun getPopularPerformances(): PerformanceListResponse {
        val performances = performanceListResponseRedisTemplate.opsForValue().get(PERFORMANCE_VIEW_CACHE_KEY)

        if (performances != null) {
            return performances
        }

        val performanceIds = getPopularPerformanceIds()

        return if (performanceIds.isNotEmpty()) {
            val popularPerformances = performanceService.getPopularPerformances(performanceIds)
            performanceListResponseRedisTemplate.opsForValue().set(PERFORMANCE_VIEW_CACHE_KEY, popularPerformances)
            popularPerformances
        } else {
            PerformanceListResponse(0, emptyList())
        }
    }

    /**
     * 상위 인기 공연 10개의 performanceId 반환
     */
    private fun getPopularPerformanceIds(): List<Long> {
        val topPerformanceIds = longRedisTemplate.opsForZSet()
            .reverseRangeWithScores(PERFORMANCE_VIEW_COUNT_KEY, 0, 9)

        return if (!topPerformanceIds.isNullOrEmpty()) {
            topPerformanceIds.mapNotNull { it.value }
        } else {
            log.info("조회된 공연이 없습니다.")
            emptyList()
        }
    }
}
