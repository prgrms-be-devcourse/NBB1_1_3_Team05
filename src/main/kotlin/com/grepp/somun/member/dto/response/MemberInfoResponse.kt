package com.grepp.somun.member.dto.response

import com.grepp.somun.member.entity.MemberEntity


@JvmRecord
data class MemberInfoResponse(
    val email: String,
    val name: String,
    val role: String
) {
    companion object {
        fun fromEntity(memberEntity: MemberEntity): MemberInfoResponse {
            return MemberInfoResponse(
                memberEntity.email,
                memberEntity.name,
                memberEntity.role.description
            )
        }
    }
}