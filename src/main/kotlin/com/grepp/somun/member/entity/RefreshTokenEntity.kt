package com.grepp.somun.member.entity

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.ToString
import lombok.experimental.SuperBuilder
import java.time.LocalDateTime

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "refresh_tokens")
class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private var tokenId: Long? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private val member: MemberEntity? = null

    // Refresh Token 필드
    @Column(name = "refresh_token", unique = true, nullable = false)
    private var refreshToken: String? = null

    // 만료 날짜 필드
    @Column(name = "expiry_date", nullable = false)
    private var expiryDate: LocalDateTime? = null
}
