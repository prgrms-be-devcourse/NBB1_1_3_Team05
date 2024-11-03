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
    val member: MemberEntity, // nullable을 제거하여 항상 유효한 MemberEntity를 요구합니다.

    // Refresh Token 필드
    @Column(name = "refresh_token", unique = true, nullable = false)
    var refreshToken: String, // nullable을 제거하여 항상 유효한 String을 요구합니다.

    // 만료 날짜 필드
    @Column(name = "expiry_date", nullable = false)
    var expiryDate: LocalDateTime // nullable을 제거하여 항상 유효한 LocalDateTime을 요구합니다.
)