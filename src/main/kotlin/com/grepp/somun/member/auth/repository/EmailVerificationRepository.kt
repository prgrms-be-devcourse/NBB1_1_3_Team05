package com.grepp.somun.member.auth.repository

import com.grepp.somun.member.auth.entity.EmailVerificationEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface EmailVerificationRepository : JpaRepository<EmailVerificationEntity, Long> {
    fun findByEmail(email: String?): Optional<EmailVerificationEntity>
    fun deleteByEmail(email: String?)
}