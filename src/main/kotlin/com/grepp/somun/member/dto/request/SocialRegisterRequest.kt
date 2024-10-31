package com.grepp.somun.member.dto.request

import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.MemberRole
import com.grepp.somun.member.entity.SocialProvider


@JvmRecord
data class SocialRegisterRequest(
    val email: String,
    val providerId: String,
    val name: String,
    val provider: SocialProvider
) {
    fun toEntity(): MemberEntity {
        return MemberEntity(
            email = this.email,
            name = this.name,
            role = MemberRole.ROLE_USER,
            provider = this.provider,
            providerId = this.providerId
        )
    }


    companion object {
        fun create(
            email: String, providerId: String,
            name: String, provider: SocialProvider
        ): SocialRegisterRequest {
            return SocialRegisterRequest(email, providerId, name, provider)
        }
    }
}
