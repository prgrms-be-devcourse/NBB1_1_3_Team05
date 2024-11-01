package com.grepp.somun.member.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.dto.request.LocalRegisterRequest
import com.grepp.somun.member.dto.request.MemberCategoryRequest
import com.grepp.somun.member.dto.request.SocialRegisterRequest
import com.grepp.somun.member.dto.response.CategoryResponse
import com.grepp.somun.member.dto.response.MemberInfoResponse
import com.grepp.somun.member.entity.MemberCategoryEntity
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.MemberRole
import com.grepp.somun.member.entity.SocialProvider
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
import com.grepp.somun.member.repository.MemberCategoryRepository
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.performance.repository.CategoryRepository
import jakarta.servlet.http.HttpSession
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberServiceImpl(
    private val memberRepository: MemberRepository,
    private val memberValidatorService: MemberValidatorService,
    private val categoryRepository: CategoryRepository,
    private val memberCategoryRepository: MemberCategoryRepository,
    private val passwordEncoder: PasswordEncoder
) : MemberService {

    @Transactional
    override fun registerBasicUser(localRegisterRequest: LocalRegisterRequest) {
        validateEmailAndCheckDuplicate(localRegisterRequest.email)
        validateNameAndCheckDuplicate(localRegisterRequest.name)
        memberValidatorService.validatePassword(localRegisterRequest.password)

        val encodedPassword = passwordEncoder.encode(localRegisterRequest.password)
        println(encodedPassword)

        val memberEntity = MemberEntity(
            email = localRegisterRequest.email,
            password = encodedPassword,
            name = localRegisterRequest.name,
            role = MemberRole.ROLE_USER,
            provider = SocialProvider.LOCAL,
        )

        memberRepository.save(memberEntity)
    }

    override fun isSocialMemberRegistered(socialMemberCheckDto: SocialMemberCheckDto): Boolean {
        val findMember = memberRepository.findByEmail(socialMemberCheckDto.email)

        return findMember.map { member ->
            if (member.providerId == null) {
                throw GeneralException(ErrorStatus.EMAIL_DUPLICATE)
            }
            val matchesProvider = member.providerId == socialMemberCheckDto.providerId
            if (!matchesProvider) {
                throw GeneralException(ErrorStatus.SOCIAL_EMAIL_DUPLICATE)
            }
            true
        }.orElse(false)
    }

    @Transactional
    override fun registerSocialMember(request: SocialRegisterRequest, session: HttpSession) {
        validateEmailAndCheckDuplicate(request.email)
        val memberEntity = request.toEntity()
        memberRepository.save(memberEntity)
        session.invalidate()
    }

    override fun validateEmailAndCheckDuplicate(email: String) {
        memberValidatorService.validateEmail(email)
        if (memberRepository.existsByEmail(email)) {
            throw GeneralException(ErrorStatus.EMAIL_DUPLICATE)
        }
    }

    override fun validateNameAndCheckDuplicate(name: String) {
        memberValidatorService.validateName(name)
        if (memberRepository.existsByName(name)) {
            throw GeneralException(ErrorStatus.NAME_DUPLICATE)
        }
    }

    override fun updateName(email: String, name: String) {
        val member = memberRepository.findByEmail(email).orElseThrow {
            GeneralException(ErrorStatus.MEMBER_NOT_FOUND)
        }
        validateNameAndCheckDuplicate(name)
        member.changeName(name)
        memberRepository.save(member)
    }

    @Transactional
    override fun memberAddCategory(memberCategoryRequest: MemberCategoryRequest, email: String) {
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        addCategories(memberCategoryRequest, memberEntity)
    }

    @Transactional
    override fun updateFavoriteCategories(memberCategoryRequest: MemberCategoryRequest, email: String) {
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        memberCategoryRepository.deleteByMemberMemberId(memberEntity.memberId!!)
        memberAddCategory(memberCategoryRequest, email)
    }

    private fun addCategories(memberCategoryRequest: MemberCategoryRequest, memberEntity: MemberEntity) {
        memberCategoryRequest.categories.forEach { categoryId ->
            val categoryEntity = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow { GeneralException(ErrorStatus.MEMBER_CATEGORY_NOT_FOUND) }

            val memberCategoryEntity = MemberCategoryEntity(
                category = categoryEntity,
                member = memberEntity
            )

            memberCategoryRepository.save(memberCategoryEntity)
        }
    }

    override fun getAllCategories(): List<CategoryResponse> {
        return categoryRepository.findAll()
            .map { CategoryResponse.fromEntity(it) }
    }

    override fun getFavoriteCategories(email: String): List<CategoryResponse> {
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        val favoriteCategories = memberCategoryRepository.findCategoriesByMemberId(memberEntity.memberId!!)
        return favoriteCategories.map { CategoryResponse.fromEntity(it) }
    }

    override fun getMemberInfoByEmail(email: String): MemberInfoResponse {
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        return MemberInfoResponse.fromEntity(memberEntity)
    }

    @Transactional
    override fun changeRoleToPadmin(email: String) {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        member.changeRole(MemberRole.ROLE_PADMIN)
        memberRepository.save(member)
    }

    override fun changeFirstLogin(email: String) {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        member.markFirstLoginComplete()
        memberRepository.save(member)
    }

    override fun getMemberIdByEmail(email: String): Long {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        return member.memberId!!
    }
}
