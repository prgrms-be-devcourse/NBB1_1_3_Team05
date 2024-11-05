package com.grepp.somun.performance.controller


import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.performance.dto.response.PerformanceQueueResponse
import com.grepp.somun.performance.service.PerformanceQueueService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/v1/performances")
class PerformanceQueueController @Autowired constructor(
    private val queueService: PerformanceQueueService
) {

    private val log = LoggerFactory.getLogger(PerformanceQueueController::class.java)

    // 대기열에 사용자 추가
    @PostMapping("/queue")
    fun enterQueue(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam("performanceId") performanceId: Long
    ): ResponseEntity<ApiResponse<PerformanceQueueResponse>> {
        val userEmail = userDetails.username
        log.info("컨트롤러 userName $userEmail")
        log.info("컨트롤러 performanceId $performanceId")

        queueService.addToQueue(userEmail, performanceId)
        val rank = queueService.getRank(userEmail, performanceId)

        val performanceQueueResponse = PerformanceQueueResponse.from(userEmail, rank)
        return ApiResponse.onSuccess(performanceQueueResponse)
    }
}
