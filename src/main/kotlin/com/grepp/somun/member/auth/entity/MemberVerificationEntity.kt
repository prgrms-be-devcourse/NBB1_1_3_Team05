package com.grepp.somun.member.auth.entity

import com.grepp.somun.member.entity.MemberEntity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@jakarta.persistence.Entity
@lombok.Getter
@NoArgsConstructor
@lombok.Builder
@AllArgsConstructor
@jakarta.persistence.Table(name = "member_verification")
class MemberVerificationEntity {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @jakarta.persistence.Column(name = "member_email", nullable = false)
    private var memberEmail: String? = null

    @jakarta.persistence.Column(name = "verification_email", nullable = false, unique = true)
    private var verificationEmail: String? = null

    @jakarta.persistence.OneToOne
    @JoinColumn(name = "member_email", referencedColumnName = "email", insertable = false, updatable = false)
    private val member: MemberEntity? = null
}
