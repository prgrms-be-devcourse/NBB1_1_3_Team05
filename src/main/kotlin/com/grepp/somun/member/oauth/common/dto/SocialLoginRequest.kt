package com.grepp.somun.member.oauth.common.dto

@JvmRecord
data class SocialLoginRequest(
    val code: String,
    val state: String
)
