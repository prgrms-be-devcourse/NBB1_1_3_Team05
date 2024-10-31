package com.grepp.somun.member.auth.service

import jakarta.mail.MessagingException
import org.springframework.transaction.annotation.Transactional

interface EmailVerificationService {
    // 인증코드 이메일 발송
    @Transactional
    @Throws(MessagingException::class)
    fun sendEmail(toEmail: String?)

    // 코드 검증
    @Transactional
    fun verifyCode(registeredEmail: String?, verificationEmail: String?, code: String?): Boolean

    // 이미 인증에 사용된 이메일인지 확인
    fun verifyEmailDuplicate(email: String?): Boolean
}
