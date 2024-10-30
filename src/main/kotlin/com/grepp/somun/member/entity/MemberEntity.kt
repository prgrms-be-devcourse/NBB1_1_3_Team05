package com.grepp.somun.member.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.auth.entity.MemberVerificationEntity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder

@jakarta.persistence.Entity
@lombok.Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@jakarta.persistence.Table(name = "member")
class MemberEntity : BaseEntity() {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "member_id")
    private var memberId: Long? = null

    @jakarta.persistence.Column(name = "email", unique = true, nullable = false)
    private var email: String? = null

    @jakarta.persistence.Column(name = "password")
    private var password: String? = null

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(name = "provider", nullable = false)
    private var provider: SocialProvider? = null

    @jakarta.persistence.Column(name = "provider_id")
    private var providerId: String? = null

    @jakarta.persistence.Column(name = "name", unique = true, nullable = false)
    private var name: String? = null

    @jakarta.persistence.Column(name = "first_login", nullable = false)
    private var isFirstLogin = true

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(name = "role", nullable = false)
    private var role: MemberRole? = null

    @lombok.Builder.Default
    @jakarta.persistence.OneToMany(
        mappedBy = "member",
        cascade = [jakarta.persistence.CascadeType.ALL],
        orphanRemoval = true
    )
    private val memberCategoryList: List<MemberCategoryEntity> = java.util.ArrayList<MemberCategoryEntity>()

    @jakarta.persistence.OneToOne(
        mappedBy = "member",
        cascade = [jakarta.persistence.CascadeType.ALL],
        orphanRemoval = true
    )
    private val memberVerification: MemberVerificationEntity? = null

    fun changeName(newName: String?) {
        this.name = newName
    }

    fun changeRole(newRole: MemberRole?) {
        this.role = newRole
    }

    fun markFirstLoginComplete() {
        this.isFirstLogin = false
    }
}