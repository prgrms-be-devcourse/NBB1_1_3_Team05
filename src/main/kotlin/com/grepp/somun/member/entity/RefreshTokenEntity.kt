package com.grepp.somun.member.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "refresh_tokens")
data class RefreshTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    var tokenId: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity? = null,

    // Refresh Token 필드
    @Column(name = "refresh_token", unique = true, nullable = false)
    var refreshToken: String? = null,

    // 만료 날짜 필드
    @Column(name = "expiry_date", nullable = false)
    var expiryDate: LocalDateTime? = null
)