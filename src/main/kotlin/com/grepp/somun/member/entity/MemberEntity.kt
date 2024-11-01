package com.grepp.somun.member.entity

import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.auth.entity.MemberVerificationEntity
import jakarta.persistence.*

@Entity
@Table(name = "member")
class MemberEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val memberId: Long,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "password")
    var password: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    var provider: SocialProvider,

    @Column(name = "provider_id")
    var providerId: String? = null,

    @Column(name = "name", unique = true, nullable = false)
    var name: String,

    @Column(name = "first_login", nullable = false)
    var isFirstLogin: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: MemberRole = MemberRole.ROLE_USER,

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberCategoryList: List<MemberCategoryEntity> = mutableListOf(),

    @OneToOne(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberVerification: MemberVerificationEntity? = null
) : BaseEntity() {

    fun changeName(newName: String) {
        this.name = newName
    }

    fun changeRole(newRole: MemberRole) {
        this.role = newRole
    }

    fun markFirstLoginComplete() {
        this.isFirstLogin = false
    }
}
