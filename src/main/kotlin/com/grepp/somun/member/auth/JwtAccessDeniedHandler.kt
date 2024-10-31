package com.grepp.somun.member.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 사용자가 권한이 없을 때 403 Forbidden 상태 코드를 응답으로 보내는 핸들러
 * @author yeonsu
 */
@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    private val objectMapper = ObjectMapper()
    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        // 응답 설정
        response.status = HttpServletResponse.SC_FORBIDDEN // 403 상태 코드 설정
        response.contentType = "application/json" // JSON 형식으로 설정
        response.characterEncoding = "UTF-8" // 문자 인코딩 설정

        // JSON 형식의 에러 메시지 맵 생성
        val errorResponse: MutableMap<String, String> = HashMap()
        errorResponse["error"] = "Access denied"
        errorResponse["message"] = "권한이 없는 사용자입니다."

        // 객체를 JSON 문자열로 변환하여 응답 본문에 작성
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
