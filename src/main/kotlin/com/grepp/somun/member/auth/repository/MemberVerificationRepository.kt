package com.grepp.somun.member.auth.repository

import com.grepp.somun.member.auth.entity.MemberVerificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberVerificationRepository : JpaRepository<MemberVerificationEntity?, Long?> {
    fun existsByVerificationEmail(verificationEmail: String?): Boolean
}
