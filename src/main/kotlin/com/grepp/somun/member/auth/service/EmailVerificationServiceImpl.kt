package com.grepp.somun.member.auth.service

import com.grepp.somun.config.logger
import com.grepp.somun.member.auth.entity.EmailVerificationEntity
import com.grepp.somun.member.auth.entity.MemberVerificationEntity
import com.grepp.somun.member.auth.repository.EmailVerificationRepository
import com.grepp.somun.member.auth.repository.MemberVerificationRepository
import com.grepp.somun.member.service.MemberService
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import java.util.function.ObjIntConsumer

/**
 * @author kim yechan
 * 공연관리자 권한 획득을 위한 이메일 인증
 */
@Service
@Slf4j
class EmailVerificationServiceImpl(
    private val javaMailSender: JavaMailSender,
    private val emailVerificationRepository: EmailVerificationRepository,
    private val memberVerificationRepository: MemberVerificationRepository,
    private val memberService: MemberService,
    private val templateEngine: TemplateEngine,
    @Value("\${mail.username}") private val senderEmail: String
) : EmailVerificationService {

    private val logger = logger()


    // 보낼 이메일 폼 생성, 이미 DB에 있다면 삭제
    @Transactional
    @Throws(MessagingException::class)
    fun createEmailForm(email: String?): MimeMessage {
        val authCode = createCode()
        val message: MimeMessage = javaMailSender.createMimeMessage()
        message.addRecipients(MimeMessage.RecipientType.TO, email)
        message.subject = "인증번호를 somun페이지에 입력해주세요."
        message.setFrom(senderEmail)
        message.setText(setContext(authCode), "utf-8", "html")

        // DB에 인증코드 저장 (기존 인증 정보 삭제 후 새로 생성)
        emailVerificationRepository.findByEmail(email)?.ifPresent { existingVerification ->
            emailVerificationRepository.deleteByEmail(email)
            logger.info("이전에 신청한 이메일 인증 정보를 삭제했습니다. {}", email)
        }
        val emailVerification = email?.let { EmailVerificationEntity(it, authCode, 5L) }
        if (emailVerification != null) {
            emailVerificationRepository.save(emailVerification)
        }
        return message
    }

    // 랜덤으로 6자리 인증 코드 생성
    private fun createCode(): String {
        val leftLimit = 48 // number '0'
        val rightLimit = 122 // alphabet 'z'
        val targetStringLength = 6
        val random = Random()
        return random.ints(leftLimit, rightLimit + 1)
            .filter { i: Int -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97) }
            .limit(targetStringLength.toLong())
            .collect<StringBuilder>(
                { StringBuilder() },
                ObjIntConsumer<StringBuilder> { obj: StringBuilder, codePoint: Int ->
                    obj.appendCodePoint(codePoint)
                }
            ) { obj: StringBuilder, s: StringBuilder? -> obj.append(s) }
            .toString()
    }

    // 보낼 이메일 내용 초기화
    private fun setContext(code: String): String {
        val context = Context()
        context.setVariable("code", code)
        return templateEngine.process("mail", context)
    }

    /**
     * 이메일로 인증코드 발송
     * @param toEmail
     * @throws MessagingException
     */
    @Transactional
    @Throws(MessagingException::class)
    override fun sendEmail(toEmail: String?) {
        val emailForm: MimeMessage = createEmailForm(toEmail)
        javaMailSender.send(emailForm)
    }

    /**
     * 이메일 인증을 하는 사용자가 보내온 code를 인증
     * 만약 맞다면 해당 회원의 권한을 공연관리자로 수정
     * @param registeredEmail
     * @param verificationEmail
     * @param code
     * @return
     */
    @Transactional
    override fun verifyCode(registeredEmail: String?, verificationEmail: String?, code: String?): Boolean {
        val emailVerificationEntity: EmailVerificationEntity =
            emailVerificationRepository.findByEmail(verificationEmail)
                ?.orElseThrow { RuntimeException("해당 이메일의 인증시도 데이터가 존재하지않습니다.") }
                ?: throw RuntimeException("emailVerificationEntity가 null입니다.")

        // 인증 성공(코드의 유효시간과 인증코드를 확인)
        if (!emailVerificationEntity.isExpired && emailVerificationEntity.verificationCode == code) {
            // MemberVerification 테이블에 추가
            logger.info("인증코드 성공")
            val memberVerificationEntity = MemberVerificationEntity(
                verificationEmail = verificationEmail,
                memberEmail = registeredEmail
            )
            memberVerificationRepository.save(memberVerificationEntity)

            // 유저의 권한을 ROLE_PADMIN으로 변경
            registeredEmail?.let {
                memberService.changeRoleToPadmin(it)
            } ?: throw IllegalArgumentException("등록된 이메일이 유효하지 않습니다.")

            logger.info("유저 권한 변경")

            // 기존 EmailVerification 테이블에서 삭제
            emailVerificationRepository.deleteByEmail(verificationEmail)
            return true
        }
        return false
    }

    /**
     * 이메일 인증에 사용된 이메일인지 확인
     * @param email
     * @return
     */
    override fun verifyEmailDuplicate(email: String?): Boolean {
        return memberVerificationRepository.existsByVerificationEmail(email)
    }
}