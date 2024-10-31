package com.grepp.somun.member.auth

import com.grepp.somun.config.logger
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

@Slf4j
@RequiredArgsConstructor
class JwtFilter(private val jwtTokenProvider: JwtTokenProvider) : GenericFilterBean() {

    private val logger = logger()

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        val httpServletRequest = servletRequest as HttpServletRequest
        val httpServletResponse = servletResponse as HttpServletResponse
        val accessToken = resolveToken(httpServletRequest)
        val requestURI = httpServletRequest.requestURI
        try {
            if (StringUtils.hasText(accessToken)) {
                jwtTokenProvider!!.validateToken(accessToken) // 토큰 검증
                val authentication = jwtTokenProvider.getAuthentication(accessToken) // 인증 객체 생성
                SecurityContextHolder.getContext().authentication = authentication
                if (authentication != null) {
                    logger.debug("Security Context에 '${authentication.name}' 인증 정보를 저장했습니다, uri: $requestURI")
                }
            } else {
                logger.debug("JWT 토큰이 없습니다., uri: $requestURI")
            }
        } catch (e: ExpiredJwtException) {
            logger.info("만료된 JWT 토큰입니다.")
            sendErrorResponse(httpServletResponse, "만료된 액세스 토큰입니다.")
            return  // 에러 발생 시 더 이상 필터 체인을 호출하지 않음
        } catch (e: MalformedJwtException) {
            logger.info("잘못된 JWT 서명입니다.")
            sendErrorResponse(httpServletResponse, "잘못된 JWT 서명입니다.")
            return
        } catch (e: SecurityException) {
            logger.info("잘못된 JWT 서명입니다.")
            sendErrorResponse(httpServletResponse, "잘못된 JWT 서명입니다.")
            return
        } catch (e: UnsupportedJwtException) {
            logger.info("지원되지 않는 JWT 토큰입니다.")
            sendErrorResponse(httpServletResponse, "지원되지 않는 JWT 토큰입니다.")
            return
        } catch (e: IllegalArgumentException) {
            logger.info("JWT 토큰이 잘못되었습니다.")
            sendErrorResponse(httpServletResponse, "JWT 토큰이 잘못되었습니다.")
            return
        }
        filterChain.doFilter(servletRequest, servletResponse)
    }

    // JWT 토큰 정보 추출
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    // 401 에러와 함께 JSON 메시지를 반환하는 메서드
    @Throws(IOException::class)
    private fun sendErrorResponse(response: HttpServletResponse, message: String) {
        if (response.isCommitted) {
            logger.debug("응답이 이미 커밋되어서 메시지 추가 불가")
        }
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write("{\"error\": \"$message\"}")
    }

    companion object {
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}
