package com.grepp.somun.member.auth.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.auth.JwtTokenProvider
import com.grepp.somun.member.auth.dto.TokenResponseDTO
import com.grepp.somun.member.entity.RefreshTokenEntity
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.member.repository.RefreshTokenRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AuthServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val customUserDetailsService: CustomUserDetailsService
) : AuthService {

    override fun validateRefreshToken(refreshToken: String): Boolean {
        val email = jwtTokenProvider.getMemberEmailFromToken(refreshToken)
        val member = memberRepository.findByEmail(email).orElseThrow {
            GeneralException(ErrorStatus.MEMBER_NOT_FOUND)
        }
        val storedRefreshToken = refreshTokenRepository.findByMember(member).orElseThrow {
            GeneralException(ErrorStatus.INVALID_TOKEN)
        }

        // 현재 시간과 storedExpiryDate 비교
        val storedExpiryDate = storedRefreshToken.expiryDate
        val now = LocalDateTime.now()
        return !now.isAfter(storedExpiryDate) && storedRefreshToken.refreshToken == refreshToken
    }

    override fun createNewAccessToken(refreshToken: String): String {
        val authentication = jwtTokenProvider.getAuthentication(refreshToken)
        return jwtTokenProvider.createAccessToken(authentication)
    }

    override fun insertRefreshToken(refreshToken: String) {
        val email = jwtTokenProvider.getMemberEmailFromToken(refreshToken)
        val member = memberRepository.findByEmail(email).orElseThrow {
            GeneralException(ErrorStatus.MEMBER_NOT_FOUND)
        }

        // member에 해당하는 리프레시 토큰 삭제
        refreshTokenRepository.deleteAllByMember(member)
        val expiryDate = jwtTokenProvider.getExpiryDate(refreshToken)
        val refreshTokenEntity = RefreshTokenEntity(
            refreshToken = refreshToken,
            member = member,
            expiryDate = expiryDate
        )
        refreshTokenRepository.save(refreshTokenEntity)
    }

    override fun createTokenResponseForSocialMember(email: String): TokenResponseDTO {
        val memberEntity = memberRepository.findByEmail(email).orElseThrow {
            RuntimeException("사용자가 존재하지 않습니다.")
        }

        val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(email)
        val authentication = PreAuthenticatedAuthenticationToken(
            userDetails, null, userDetails.authorities
        )
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken(email)
        insertRefreshToken(refreshToken)

        val message = "액세스 토큰과 리프레시 토큰이 정상적으로 발급되었습니다."
        return TokenResponseDTO(
            accessToken, refreshToken, message, memberEntity.name, memberEntity.isFirstLogin
        )
    }
}