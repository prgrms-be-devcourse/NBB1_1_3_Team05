package com.grepp.somun.member.auth.service

import com.grepp.somun.member.auth.dto.TokenResponseDTO

interface AuthService {
    fun validateRefreshToken(refreshToken: String?): Boolean
    fun createNewAccessToken(refreshToken: String?): String?
    fun insertRefreshToken(refreshToken: String?)
    fun createTokenResponseForSocialMember(email: String?): TokenResponseDTO?
}