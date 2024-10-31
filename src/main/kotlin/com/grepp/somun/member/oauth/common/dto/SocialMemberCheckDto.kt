package com.grepp.somun.member.oauth.common.dto

import com.grepp.somun.member.entity.SocialProvider

/**
 * 소셜 사용자가 가입되어있지 않았을 때, 회원가입을 위해 필요한 정보
 */
data class SocialMemberCheckDto(
    val email: String,
    val providerId: String,
    val provider: SocialProvider
) {
    companion object {
        fun create(email: String?, providerId: String?, provider: SocialProvider?): SocialMemberCheckDto {
            return SocialMemberCheckDto(
                email = email ?: throw IllegalArgumentException("email is required"),
                providerId = providerId ?: throw IllegalArgumentException("providerId is required"),
                provider = provider ?: throw IllegalArgumentException("provider is required")
            )
        }
    }
}
