package com.grepp.somun.member.auth.controller

import com.grepp.somun.global.apiResponse.ApiResponse
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.auth.dto.CodeRequestDto
import com.grepp.somun.member.auth.dto.EmailRequestDto
import com.grepp.somun.member.auth.service.EmailVerificationService
import com.grepp.somun.member.validator.EmailValidator
import jakarta.mail.MessagingException
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/emails")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService,
    private val emailValidator: EmailValidator
) {
    private val log = LoggerFactory.getLogger(EmailVerificationController::class.java)

    /**
     * 입력한 이메일로 인증코드를 요청
     * @param emailRequestDto
     * @param session
     * @return
     * @throws MessagingException
     */
    @PostMapping("/code")
    @Throws(MessagingException::class)
    fun getVerifyCode(
        @RequestBody emailRequestDto: EmailRequestDto, session: HttpSession
    ): ResponseEntity<ApiResponse<Void>> {
        val email = emailRequestDto.email

        // 이메일 형식 검증
        if (!emailValidator.isValidEmail(email)) {
            throw GeneralException(ErrorStatus.EMAIL_INVALID)
        }

        // 이미 인증에 사용된 이메일인지 확인
        if (emailVerificationService.verifyEmailDuplicate(email)) {
            throw GeneralException(ErrorStatus.VERIFICATION_EMAIL_DUPLICATE)
        }
        session.setAttribute("verifyEmail", email)
        session.maxInactiveInterval = 600 // 세션 10분간 유지
        emailVerificationService.sendEmail(email)
        return ApiResponse.onSuccess()
    }

    /**
     * 이메일 인증을 위한 코드 인증
     * @param codeRequestDto
     * @param userDetails
     * @param session
     * @return
     */
    @PostMapping("/verify")
    fun verifyEmail(
        @RequestBody codeRequestDto: CodeRequestDto,
        @AuthenticationPrincipal userDetails: UserDetails,
        session: HttpSession
    ): ResponseEntity<ApiResponse<Void>> {
        val code = codeRequestDto.code
        if (session.getAttribute("verifyEmail") == null || userDetails.username == null) {
            throw GeneralException(ErrorStatus._BAD_REQUEST)
        }
        if (emailVerificationService.verifyCode(
                userDetails.username, session.getAttribute("verifyEmail") as String, code
            )
        ) {
            session.invalidate()
            return ApiResponse.onSuccess()
        }
        throw GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR)
    }
}