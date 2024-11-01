package com.grepp.somun.member.auth.dto

import lombok.AllArgsConstructor
import lombok.Data

@Data
@AllArgsConstructor
class TokenResponseDTO {
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var message: String? = null
    private val userName: String? = null
    private val isFirstLogin = false

    constructor(message: String?) {
        this.message = message
    }

    constructor(accessToken: String?, refreshToken: String?) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }
}
