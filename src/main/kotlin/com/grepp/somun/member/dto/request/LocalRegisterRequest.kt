package com.grepp.somun.member.dto.request

import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.MemberRole
import com.grepp.somun.member.entity.SocialProvider


@JvmRecord
data class LocalRegisterRequest(
    val email: String,
    val password: String,
    val name: String
) {
    fun toEntity(): MemberEntity {
        return MemberEntity(
            email = this.email,
            password = this.password,
            name = this.name,
            role = MemberRole.ROLE_USER,
            provider = SocialProvider.LOCAL
        )
    }
}
