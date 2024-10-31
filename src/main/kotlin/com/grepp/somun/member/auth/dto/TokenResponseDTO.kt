package com.grepp.somun.member.auth.dto

data class TokenResponseDTO(
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var message: String? = null,
    val userName: String? = null,
    val isFirstLogin: Boolean = false
)
