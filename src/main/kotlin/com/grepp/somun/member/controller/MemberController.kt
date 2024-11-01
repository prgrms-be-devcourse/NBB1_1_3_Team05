package com.grepp.somun.member.controller


import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.dto.request.LocalRegisterRequest
import com.grepp.somun.member.dto.request.MemberCategoryRequest
import com.grepp.somun.member.dto.response.CategoryResponse
import com.grepp.somun.member.dto.response.MemberInfoResponse
import com.grepp.somun.member.service.MemberService
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
class MemberController (
    private val memberService: MemberService
){

    /**
     * 이메일 중복 체크
     *
     * @return true면 중복x
     */
    @GetMapping("/validation/email/{email}")
    fun checkEmail(@PathVariable("email") email: String): ResponseEntity<ApiResponse<Boolean>> {
        memberService.validateEmailAndCheckDuplicate(email)
        return ApiResponse.onSuccess(true)
    }

    /**
     * 닉네임 중복 체크
     *
     * @param name
     * @return true면 중복x
     */
    @GetMapping("/validation/name/{name}")
    fun checkName(@PathVariable("name") name: String): ResponseEntity<ApiResponse<Boolean>> {
        memberService.validateNameAndCheckDuplicate(name)
        return ApiResponse.onSuccess(true)
    }


    /**
     * 일반사용자 회원가입
     *
     * @param localRegisterRequest
     * @return 성공 200ok
     */
    @PostMapping("/register")
    fun register(
        @RequestBody localRegisterRequest: LocalRegisterRequest
    ): ResponseEntity<ApiResponse<Void>> {
        memberService.registerBasicUser(localRegisterRequest)
        return ApiResponse.onSuccess()
    }

    /**
     * 닉네임 변경
     *
     * @param name
     * @param token
     * @return 성공 200, 닉네임중복409 , 닉네임 형식안맞음400
     */
    @PatchMapping("/name/{name}")
    fun updateNickName(
        @PathVariable name: String, @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        memberService.updateName(userDetails.username, name)
        return ApiResponse.onSuccess()
    }

    /**
     * 사용자 선호 카테고리 등록
     *
     * @param memberCategoryRequest
     * @return 성공 200, 카테고리 및 회원 못찾음 404
     */
    @PostMapping("/categories")
    fun addFavoriteCategories(
        @RequestBody memberCategoryRequest: MemberCategoryRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        if (memberCategoryRequest.categories.size > 3) {
            throw GeneralException(ErrorStatus._BAD_REQUEST)
        }

        memberService.memberAddCategory(memberCategoryRequest, userDetails.username)
        return ApiResponse.onSuccess()
    }


    @get:GetMapping("/categories")
    val allCategories: ResponseEntity<ApiResponse<List<CategoryResponse>>>
        /**
         * 카테고리 전체 목록 조회
         *
         * @return categoryId, nameKr, nameEn
         */
        get() {
            val allCategories: List<CategoryResponse> = memberService.getAllCategories()
            return ApiResponse.onSuccess(allCategories)
        }


    /**
     * 사용자 선호 카테고리 조회
     *
     * @param token
     * @return
     */
    @GetMapping("/categories/favorites")
    fun getFavoriteCategories(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<List<CategoryResponse>>> {
        val email: String = userDetails.getUsername()
        val favoriteCategories: List<CategoryResponse> = memberService.getFavoriteCategories(email)
        return ApiResponse.onSuccess(favoriteCategories)
    }


    /**
     * 사용자 선호 카테고리 수정
     *
     * @param memberCategoryRequest
     * @return
     */
    @PutMapping("/categories/favorites")
    fun updateFavoriteCategories(
        @RequestBody memberCategoryRequest: MemberCategoryRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        if (memberCategoryRequest.categories.size > 3) {
            throw GeneralException(ErrorStatus._BAD_REQUEST)
        }
        memberService.updateFavoriteCategories(memberCategoryRequest, userDetails.getUsername())

        return ApiResponse.onSuccess()
    }


    /**
     * 사용자 정보 조회
     *
     * @param userDetails
     * @return email, name, role
     */
    @GetMapping
    fun getMemberInfoByEmail(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<MemberInfoResponse>> {
        val email: String = userDetails.getUsername()
        return ApiResponse.onSuccess(memberService.getMemberInfoByEmail(email))
    }


    /**
     * 첫 로그인 시 첫 로그인 여부 변경
     *
     * @param userDetails
     * @return
     */
    @PatchMapping("/first-login")
    fun changeFirstLogin(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ApiResponse<Void>> {
        val email: String = userDetails.getUsername()
        memberService.changeFirstLogin(email)
        return ApiResponse.onSuccess()
    }
}
