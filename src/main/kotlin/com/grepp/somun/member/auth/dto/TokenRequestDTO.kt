package com.grepp.somun.member.auth.dto

import lombok.Data

@Data
class TokenRequestDTO {
    private val refreshToken: String? = null
}
