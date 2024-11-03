package com.grepp.somun.member.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 인증되지 않은 사용자가 보호된 리소스에 접근하려 할 때 401 Unauthorized 상태 코드를 응답으로 보내는 핸들러
 * @author yeonsu
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    private val objectMapper = ObjectMapper() // JSON 변환기
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val message = request.getAttribute("message") as String

        // 응답 설정
        response.status = HttpServletResponse.SC_UNAUTHORIZED // 401 상태 코드 설정
        response.contentType = "application/json" // JSON 형식으로 설정
        response.characterEncoding = "UTF-8" // 문자 인코딩 설정

        // JSON 형식의 에러 메시지 맵 생성
        val errorResponse: MutableMap<String, String> = HashMap()
        errorResponse["error"] = "Unauthorized"
        errorResponse["message"] = message
        val accessToken = request.getAttribute("accessToken") as String
        val refreshToken = request.getAttribute("refreshToken") as String
        errorResponse["accessToken"] = accessToken
        errorResponse["refreshToken"] = refreshToken

        // 객체를 JSON 문자열로 변환하여 응답 본문에 작성
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
