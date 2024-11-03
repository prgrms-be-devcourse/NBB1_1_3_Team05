package com.grepp.somun.member.auth.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "email_verification")
data class EmailVerificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val verificationCode: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val expiresAt: LocalDateTime
) {
    constructor(email: String, verificationCode: String, expirationMinutes: Long) : this(
        email = email,
        verificationCode = verificationCode,
        createdAt = LocalDateTime.now(),
        expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes)
    )

    fun isExpired(): Boolean {
        return !LocalDateTime.now().isAfter(this.expiresAt)
    }
}