package com.grepp.somun.member.auth.controller

import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.global.apiResponse.success.SuccessStatus
import com.grepp.somun.member.auth.JwtFilter
import com.grepp.somun.member.auth.JwtTokenProvider
import com.grepp.somun.member.auth.dto.TokenRequestDTO
import com.grepp.somun.member.auth.dto.TokenResponseDTO
import com.grepp.somun.member.auth.service.AuthService
import com.grepp.somun.member.dto.LoginDTO
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.repository.MemberRepository
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/members")
class AuthController(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val authService: AuthService,
    private val memberRepository: MemberRepository
) {
    /**
     * 로컬 로그인
     * @param loginDTO
     * @return accessToken, refreshToken, message
     */
    @PostMapping("/authenticate")
    fun authorize(@RequestBody loginDTO: LoginDTO): ResponseEntity<TokenResponseDTO> {
        val authenticationToken = UsernamePasswordAuthenticationToken(loginDTO.email, loginDTO.password)

        val memberEntity = memberRepository.findByEmail(loginDTO.email)
            .orElseThrow { RuntimeException("사용자가 존재하지 않습니다.") }

        val authentication: Authentication = authenticationManagerBuilder.getObject()?.authenticate(authenticationToken)
            ?: throw RuntimeException("인증 관리자 객체를 가져오는 데 실패했습니다.")

        SecurityContextHolder.getContext().authentication = authentication

        val accessToken = jwtTokenProvider.createAccessToken(authentication)

        val email = authentication.name

        val refreshToken = jwtTokenProvider.createRefreshToken(email)

        authService.insertRefreshToken(refreshToken)

        val httpHeaders = HttpHeaders()
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer $accessToken")

        val message = "액세스 토큰과 리프레시 토큰이 정상적으로 발급되었습니다."

        return ResponseEntity(
            TokenResponseDTO(accessToken, refreshToken, message, memberEntity.name, memberEntity.isFirstLogin),
            httpHeaders,
            HttpStatus.OK
        )
    }

    //토큰 유효성 검사 테스트용
    @PostMapping("/check")
    fun checkToken() {
        println("토큰 체크")
    }

    /**
     * 리프레시 토큰 유효성 검사
     * @param tokenRequestDTO
     * @return accessToken, refreshToken
     */
    @PostMapping("/validate")
    fun validateRefreshToken(@RequestBody tokenRequestDTO: TokenRequestDTO): ResponseEntity<ApiResponse<TokenResponseDTO>> {
        val refreshToken: String = tokenRequestDTO.refreshToken ?: throw GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN)

        if (!authService.validateRefreshToken(refreshToken)) { // 리프레시 토큰 유효하지 않을 때
            throw GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN)
        }

        val newAccessToken: String = authService.createNewAccessToken(refreshToken)
        val result = TokenResponseDTO(newAccessToken, refreshToken)
        return ApiResponse.onSuccess(HttpStatus.CREATED, "COMMON200", SuccessStatus._CREATED.message, result)
    }
}
