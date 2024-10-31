package com.grepp.somun.member.auth

import com.grepp.somun.config.logger
import com.grepp.somun.member.auth.service.CustomUserDetailsService
import com.grepp.somun.member.repository.MemberRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.function.Supplier

/**
 * JWT 토큰 관리 클래스
 *
 * @author yeonsu
 */
@Component
@Slf4j
class JwtTokenProvider(
    // JWT 비밀키
    @param:Value("\${jwt.secret}") private val secretKey: String,
    // 액세스 토큰 유효 시간
    @param:Value("\${jwt.access.token.expiration}") private val accessTokenValidity: Long,
    // 리프레시 토큰 유효 시간
    @param:Value("\${jwt.refresh.token.expiration}") private val refreshTokenValidity: Long,
    private val customUserDetailsService: CustomUserDetailsService,
    private val memberRepository: MemberRepository
) {
    private val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)) // JWT 서명에 사용할 키
    private val logger = logger() // 로거 인스턴스

    init {
        logger.info("JWT TokenProvider 초기화 완료")
    }

    // 액세스 토큰 생성(role 권한 부여 설정)
    fun createAccessToken(authentication: Authentication): String {
        val principal = authentication.principal

        // UserDetails에서 권한 정보 가져오기
        val authority: String
        val email: String

        // 소셜 로그인 사용자인 경우와 일반 사용자 구분
        if (principal is UserDetails) {
            val userDetails: UserDetails = principal as UserDetails
            authority = userDetails.authorities.stream()
                .map<String> { obj: GrantedAuthority -> obj.authority }
                .findFirst()
                .orElseThrow<RuntimeException>(Supplier<RuntimeException> { RuntimeException("User has no roles assigned") })
            email = userDetails.username
        } else {
            // 소셜 로그인 사용자의 경우 principal은 email 문자열이므로, 이메일을 직접 사용
            email = principal as String
            authority = authentication.authorities.stream()
                .map<String> { obj: GrantedAuthority -> obj.authority }
                .findFirst()
                .orElseThrow<RuntimeException> { RuntimeException("Social user has no roles assigned") }
        }

        // JWT 생성
        return Jwts.builder()
            .setSubject(email) // 페이로드 주제 정보
            .claim(AUTHORITIES_KEY, authority) // 권한 정보 저장
            .claim("email", email) // 이메일 정보 추가
            .signWith(key, SignatureAlgorithm.HS256) // 서명 설정
            .setExpiration(Date(System.currentTimeMillis() + accessTokenValidity)) // 만료 시간 설정
            .compact()
    }

    // 리프레시 토큰 생성
    fun createRefreshToken(email: String?): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenValidity)) // 만료 시간 설정
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    // 토큰 유효성 검사
    @Throws(JwtException::class)
    fun validateToken(token: String?) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
    }

    // 토큰으로 사용자 이메일 가져오기
    fun getMemberEmailFromToken(token: String): String? {
        return try {
            val claims: Claims = getClaims(token)
            claims.subject // 이메일은 subject로 설정
        } catch (e: JwtException) {
            logger.info("유효하지 않은 JWT 토큰입니다.")
            null
        } catch (e: IllegalArgumentException) {
            logger.info("유효하지 않은 JWT 토큰입니다.")
            null
        }
    }

    //토큰에서 만료시간 가져오기
    fun getExpiryDate(token: String): LocalDateTime? {
        return try {
            val claims: Claims = getClaims(token)
            claims
                .expiration
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        } catch (e: JwtException) {
            logger.info("유효하지 않은 JWT 토큰입니다.")
            null
        } catch (e: IllegalArgumentException) {
            logger.info("유효하지 않은 JWT 토큰입니다.")
            null
        }
    }

    //리프레시 토큰으로 Authentication 객체 얻기
    fun getAuthentication(refreshToken: String?): Authentication? {
        if (refreshToken != null) {
            // 토큰에서 Claims 추출
            val claims: Claims = getClaims(refreshToken)

            // 이메일 추출
            val email: String = claims.getSubject()

            // UserDetailsService를 통해 사용자 정보 가져오기
            val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(email)

            // Authentication 객체 생성
            return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
        }
        return null // 토큰이 유효하지 않은 경우 null 반환
    }

    private fun getClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
    }

    companion object {
        private const val AUTHORITIES_KEY = "auth" // 권한 키
    }
}
