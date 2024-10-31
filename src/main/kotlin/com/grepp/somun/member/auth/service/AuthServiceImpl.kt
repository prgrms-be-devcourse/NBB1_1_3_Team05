package com.grepp.somun.member.auth.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.auth.JwtTokenProvider
import com.grepp.somun.member.auth.dto.TokenResponseDTO
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.RefreshTokenEntity
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.member.repository.RefreshTokenRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@RequiredArgsConstructor
class AuthServiceImpl : AuthService {
    private val jwtTokenProvider: JwtTokenProvider? = null
    private val memberRepository: MemberRepository? = null
    private val refreshTokenRepository: RefreshTokenRepository? = null
    private val customUserDetailsService: CustomUserDetailsService? = null

    /**
     * 리프레시 토큰 유효성 검사
     * - DB에 저장되어 있는 리프레시 토큰과 비교
     * - 액세스 토큰으로 email 추출
     * - email로 memberEntity
     * - memberId로 DB에 저장되어 있는 리프레시 토큰 추출
     * - 두 리프레시 토큰이 일치하고, 리프레시 토큰의 유효기간이 유효하면 리프레시 토큰 유효
     * - 그렇지 않으면 리프레시 토큰 유효 X
     *
     * @param refreshToken
     * @return
     */
    override fun validateRefreshToken(refreshToken: String?): Boolean {
        val email: String? = refreshToken?.let { jwtTokenProvider?.getMemberEmailFromToken(it) }
        val member: MemberEntity = memberRepository?.findByEmail(email)?.orElse(null) ?: return false
        val storedRefreshToken: RefreshTokenEntity =
            refreshTokenRepository?.findByMember(member)?.orElse(null) ?: return false

        //현재 시간이랑 storedExpiryDate랑 비교
        val storedExpiryDate: LocalDateTime? = storedRefreshToken.expiryDate
        val now = LocalDateTime.now() // 현재 시간 가져오기
        return if (now.isAfter(storedExpiryDate)) false else storedRefreshToken.refreshToken
            .equals(refreshToken)
    }

    /**
     * 리프레시 토큰으로 새로운 토큰 발급
     *
     * @param refreshToken
     * @return
     */
    override fun createNewAccessToken(refreshToken: String?): String? {
        val authentication: Authentication? = jwtTokenProvider?.getAuthentication(refreshToken)

        // 새로운 액세스 토큰 생성
        return authentication?.let { jwtTokenProvider?.createAccessToken(it) }
    }

    /**
     * 리프레시 토큰 저장
     * @param refreshToken
     */
    override fun insertRefreshToken(refreshToken: String?) {
        val email: String? = refreshToken?.let { jwtTokenProvider?.getMemberEmailFromToken(it) }
        val member: MemberEntity? = memberRepository?.findByEmail(email)
            ?.orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        //member에 해당하는 리프레시 토큰 삭제
        refreshTokenRepository?.deleteAllByMember(member)
        val expiryDate: LocalDateTime? = refreshToken?.let { jwtTokenProvider?.getExpiryDate(it) }
        val refreshTokenEntity = RefreshTokenEntity(
            refreshToken = refreshToken,
            member = member,
            expiryDate = expiryDate
        )
        refreshTokenRepository?.save(refreshTokenEntity)
    }

    /**
     * 소셜 사용자의 토큰발행을 위해 사용
     * @param email
     * @return
     */
    override fun createTokenResponseForSocialMember(email: String?): TokenResponseDTO? {
        val memberEntity: MemberEntity = memberRepository?.findByEmail(email)
            ?.orElseThrow { RuntimeException("사용자가 존재하지 않습니다.") }
            ?: throw RuntimeException("memberEntity가 null입니다.")
        val userDetails: UserDetails? = email?.let { customUserDetailsService!!.loadUserByUsername(it) }
        val authentication = PreAuthenticatedAuthenticationToken(
            userDetails, null, userDetails?.authorities
        )
        val accessToken: String? = jwtTokenProvider?.createAccessToken(authentication)
        val refreshToken: String? = jwtTokenProvider?.createRefreshToken(email)
        insertRefreshToken(refreshToken)
        val message = "액세스 토큰과 리프레시 토큰이 정상적으로 발급되었습니다."
        return TokenResponseDTO(
            accessToken, refreshToken, message, memberEntity.name, memberEntity.isFirstLogin
        )
    }
}
