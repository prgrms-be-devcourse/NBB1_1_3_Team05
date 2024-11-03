package com.grepp.somun.coupon.controller

import com.grepp.somun.config.logger
import com.grepp.somun.coupon.dto.response.CouponRequestDto
import com.grepp.somun.coupon.dto.response.CouponResponseDto
import com.grepp.somun.coupon.service.CouponService
import com.grepp.somun.global.apiResponse.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/**
 * 쿠폰 데이터 컨트롤러
 *
 * @author ycjung
 */
@RestController
@RequestMapping("/api/v1/coupons")
class CouponController(
    private val couponService: CouponService,
) {
    private val logger = logger()

    @GetMapping
    fun getAllCouponsByMemberEmail(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam performanceId: Long?
    ): ResponseEntity<ApiResponse<List<CouponResponseDto>>> {
        logger.info("Received request to get all coupons by member email")
        return ApiResponse.onSuccess(couponService.getAllCouponsByMemberEmail(userDetails.username, performanceId))
    }

    // 선착순 쿠폰 발급받기
    @PatchMapping
    fun getFirstComeCoupon(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody couponRequestDto: CouponRequestDto
    ): ResponseEntity<ApiResponse<CouponResponseDto>> {
        return ApiResponse.onSuccess(
            couponService.getFirstComeCoupon(
                userDetails.username,
                couponRequestDto.performanceId
            )
        )
    }
}