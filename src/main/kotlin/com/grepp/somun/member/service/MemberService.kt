package com.grepp.somun.member.service

import com.grepp.somun.member.dto.request.LocalRegisterRequest
import com.grepp.somun.member.dto.request.MemberCategoryRequest
import com.grepp.somun.member.dto.request.SocialRegisterRequest
import com.grepp.somun.member.dto.response.CategoryResponse
import com.grepp.somun.member.dto.response.MemberInfoResponse
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
import jakarta.servlet.http.HttpSession

interface MemberService {
    // 일반사용자 회원가입
    fun registerBasicUser(localRegisterRequest: LocalRegisterRequest)

    // 소셜 사용자 가입여부 확인
    fun isSocialMemberRegistered(socialMemberCheckDto: SocialMemberCheckDto): Boolean

    // 소셜 사용자 닉네임 확인 후 회원가입
    fun registerSocialMember(request: SocialRegisterRequest, session: HttpSession)

    // 이메일 검증과 중복 체크
    fun validateEmailAndCheckDuplicate(email: String)

    // 닉네임 검증과 중복 체크
    fun validateNameAndCheckDuplicate(name: String)

    // 닉네임 변경
    fun updateName(email: String, name: String)

    // 사용자 선호 카테고리 추가
    fun memberAddCategory(memberCategoryRequest: MemberCategoryRequest, email: String)

    // 카테고리 전체 조회
    fun getAllCategories(): List<CategoryResponse>

    // 사용자 선호 카테고리 목록 조회
    fun getFavoriteCategories(email: String): List<CategoryResponse>

    // 선호 카테고리 수정
    fun updateFavoriteCategories(
        memberCategoryRequest: MemberCategoryRequest,
        email: String
    )

    // 사용자 정보 조회 - 마이페이지
    fun getMemberInfoByEmail(email: String): MemberInfoResponse

    // 공연관리자로 권한 변경
    fun changeRoleToPadmin(email: String)

    // 첫로그인 여부 변경
    fun changeFirstLogin(email: String)

    fun getMemberIdByEmail(email: String): Long
}
