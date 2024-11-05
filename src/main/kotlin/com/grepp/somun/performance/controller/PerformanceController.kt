package com.grepp.somun.performance.controller

import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.performance.dto.CategoryDto
import com.grepp.somun.performance.dto.request.PerformanceRegisterRequest
import com.grepp.somun.performance.dto.request.PerformanceUpdateRequest
import com.grepp.somun.performance.dto.response.PerformanceDetailResponse
import com.grepp.somun.performance.dto.response.PerformanceListResponse
import com.grepp.somun.performance.dto.response.PerformanceRegisterResponse
import com.grepp.somun.performance.dto.response.PerformanceUpdateResponse
import com.grepp.somun.performance.entity.PerformanceStatus
import com.grepp.somun.performance.service.ImageUploadService
import com.grepp.somun.performance.service.PerformanceService
import com.grepp.somun.performance.service.PerformanceViewCountService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/performances")
class PerformanceController(
    private val performanceService: PerformanceService,
    private val imageService: ImageUploadService,
    private val performanceViewCountService: PerformanceViewCountService
) {

    private val log = LoggerFactory.getLogger(PerformanceController::class.java)

    /**
     * @author Icecoff22
     * @param registerPerformanceRequest
     * @return 200, 등록 완료 메세지
     */
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun registerPerformance(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestPart("performanceData") registerPerformanceRequest: PerformanceRegisterRequest,
        @RequestPart(value = "imageFile", required = false) imageFile: MultipartFile?
    ): ResponseEntity<ApiResponse<PerformanceRegisterResponse>> {
        val performanceRegisterResponse = performanceService
            .registerPerformance(userDetails.username, registerPerformanceRequest, imageFile)

        return ApiResponse.onSuccess(
            HttpStatus.CREATED,
            "PERFORMANCE201",
            "공연이 등록되었습니다.",
            performanceRegisterResponse
        )
    }

    /**
     * 공연에 대한 전반적인 정보를 모두 조회.
     * 단, 확정되지 않은 공연은 조회하지 않는다.
     * @author Icecoff22
     * @param page
     * @param size
     * @return 200, 공연응답 리스트
     */
    @GetMapping
    fun getPerformanceList(
        @RequestParam(name = "page") page: Int?,
        @RequestParam(name = "size") size: Int?,
        @RequestParam(name = "category", required = false) categoryId: Long?,
        @RequestParam(name = "search", required = false) search: String?,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<ApiResponse<PerformanceListResponse>> {
        val response = if (userDetails == null) {
            performanceService.getPerformanceList(page!!, size!!, categoryId, search, null)
        } else {
            performanceService.getPerformanceList(page!!, size!!, categoryId, search, userDetails.username)
        }
        return ApiResponse.onSuccess(response)
    }

    /**
     * 특정 공연에 대한 전반적인 정보를 모두 조회.
     * @author Icecoff22
     * @param performanceId
     * @return 200, 공연응답 리스트
     */
    @GetMapping("/{performanceId}")
    fun getPerformanceById(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable("performanceId") performanceId: Long
    ): ResponseEntity<ApiResponse<PerformanceDetailResponse>> {
        val performanceDetail = performanceService
            .getPerformanceDetail(userDetails.username, performanceId)

        if (performanceDetail.status == PerformanceStatus.CONFIRMED) {
            performanceViewCountService.incrementViewCount(performanceId)
        }
        return ApiResponse.onSuccess(performanceDetail)
    }

    /**
     * 공연에 대한 정보를 수정.
     * 자신의 공연만 수정할 수 있다.
     * @author Icecoff22
     * @param performanceId
     * @return 200, 공연수정 정보
     */
    @PatchMapping("/{performanceId}")
    fun updatePerformance(
        @PathVariable("performanceId") performanceId: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody performanceUpdateRequest: PerformanceUpdateRequest
    ): ResponseEntity<ApiResponse<PerformanceUpdateResponse>> {
        val response = performanceService.updatePerformance(
            userDetails.username,
            performanceId,
            performanceUpdateRequest
        )
        return ApiResponse.onSuccess(response)
    }

    /**
     * 공연에 대한 삭제.
     * 실 삭제가 아닌 deleteAt만 업데이트 시킨다.
     * 자신의 공연만 삭제할 수 있다.
     * @author Icecoff22
     * @param performanceId
     * @return 200
     */
    @DeleteMapping("/{performanceId}")
    fun deletePerformance(
        @PathVariable("performanceId") performanceId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        performanceService.deletePerformance(userDetails.username, performanceId)
        return ApiResponse.onSuccess()
    }

    /**
     * 사용자 선호 카테고리 기반 공연 리스트 조회
     * 최대 10개의 공연 리스트 조회
     * @param userDetails
     * @return
     */
    @GetMapping("/favorites")
    fun getPerformanceListByUserCategories(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<PerformanceListResponse>> {
        val email = userDetails.username
        log.info(email)
        val performanceListByUserCategories = performanceService
            .getPerformanceListByUserCategories(email)

        return ApiResponse.onSuccess(performanceListByUserCategories)
    }

    /**
     * 조회수가 많은 공연을 최대 10개 조회
     * @return
     */
    @GetMapping("/rank")
    fun getPopularPerformances(): ResponseEntity<ApiResponse<PerformanceListResponse>> {
        val popularPerformances = performanceViewCountService.getPopularPerformances()
        return ApiResponse.onSuccess(popularPerformances)
    }

    @GetMapping("/admin/my")
    fun getPerformanceListAdmin(
        @RequestParam(name = "page") page: Int?,
        @RequestParam(name = "size") size: Int?,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<PerformanceListResponse>> {
        val response = performanceService.getMyPerformanceList(userDetails.username, page!!, size!!)
        return ApiResponse.onSuccess(response)
    }

    @GetMapping("/categories")
    fun getCategories(): ResponseEntity<ApiResponse<List<CategoryDto>>> {
        val categories = performanceService.getCategoryList()
        return ApiResponse.onSuccess(categories)
    }

    /**
     * 특정 지점 주변 공연 리스트를 조회할 수 있다.(최대20개)
     * @Author Icecoff22
     * @param latitude 위도
     * @param longitude 경도
     * @return PerformanceList
     */
    @GetMapping("/around-point")
    fun getAroundPoint(
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): ResponseEntity<ApiResponse<PerformanceListResponse?>> {
        return ApiResponse.onSuccess(performanceService.getAroundPoint(latitude, longitude, page, size))
    }
}
