package com.grepp.somun.member.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification")
data class EmailVerificationEntity(
    @field:Column(nullable = false) val email: String,
    @field:Column(nullable = false) val verificationCode: String,
    val expirationMinutes: Long?
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val createdAt: LocalDateTime = LocalDateTime.now()
    val expiresAt: LocalDateTime = createdAt.plusMinutes(expirationMinutes ?: 0)

    val isExpired: Boolean
        get() = LocalDateTime.now().isAfter(expiresAt)
}