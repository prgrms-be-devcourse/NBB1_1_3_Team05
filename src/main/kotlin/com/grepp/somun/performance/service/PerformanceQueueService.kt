package com.grepp.somun.performance.service


import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class PerformanceQueueService @Autowired constructor(
    @Qualifier("queueRedisTemplate") private val redisTemplate: RedisTemplate<String, String>
) {

    private val log = LoggerFactory.getLogger(PerformanceQueueService::class.java)
    private val MAX_QUEUE_SIZE = 200 // 큐 최대 크기 설정
    private val QUEUE_TRACKER_KEY = "active_performance_queues"

    private fun getQueueKey(performanceId: Long): String = "ticket_queue_$performanceId"

    // 대기열에 사용자 추가
    fun addToQueue(userEmail: String, performanceId: Long): Boolean {
        val queueKey = getQueueKey(performanceId)
        val currentQueueSize = redisTemplate.opsForZSet().zCard(queueKey) ?: 0L

        if (currentQueueSize >= MAX_QUEUE_SIZE) {
            throw GeneralException(ErrorStatus.QUEUE_FULL)
        }

        val timestamp = System.currentTimeMillis()
        redisTemplate.opsForZSet().add(queueKey, userEmail, timestamp.toDouble())
        redisTemplate.opsForSet().add(QUEUE_TRACKER_KEY, performanceId.toString())

        log.info("사용자 ${userEmail}가 공연 ${performanceId} 대기열에 추가되었습니다. 점수(순위): $timestamp")
        return true
    }

    // 대기열에서 사용자 순위 확인
    fun getRank(userEmail: String, performanceId: Long): Long {
        val queueKey = getQueueKey(performanceId)
        val rank = redisTemplate.opsForZSet().rank(queueKey, userEmail)
        return rank?.plus(1) ?: -1 // Redis의 rank는 0부터 시작하므로 +1
    }

    // 공연별로 대기열에서 다음 배치 처리
    fun processNextBatch(batchSize: Int, performanceId: Long): List<String> {
        val queueKey = getQueueKey(performanceId)
        val batchUsers = redisTemplate.opsForZSet().range(queueKey, 0, (batchSize - 1).toLong()) ?: emptySet()

        if (batchUsers.isEmpty()) {
            redisTemplate.opsForSet().remove(QUEUE_TRACKER_KEY, performanceId.toString())
            log.info("공연 ${performanceId}의 대기열에 사용자가 없습니다.")
            return emptyList()
        }

        val userEmails = batchUsers.map { it.toString() }
        batchUsers.forEach { user -> redisTemplate.opsForZSet().remove(queueKey, user) }

        return userEmails
    }

    // 일정 간격으로 대기열 배치 처리
    @Scheduled(fixedDelay = 10000) // 10초마다 실행
    fun scheduledProcessBatch() {
        val activePerformanceIds = redisTemplate.opsForSet().members(QUEUE_TRACKER_KEY) ?: emptySet()

        for (performanceIdStr in activePerformanceIds) {
            val performanceId = performanceIdStr.toLong()
            val processedUserEmails = processNextBatch(10, performanceId)
            if (processedUserEmails.isNotEmpty()) {
                log.info("공연 ${performanceId}의 처리된 사용자들: $processedUserEmails")
            }
        }
    }
}
