package com.grepp.somun.member.oauth.common.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto


interface SocialClient {
    @Throws(JsonProcessingException::class)
    fun getAccessToken(code: String): String {
        throw java.lang.UnsupportedOperationException("해당 provider에서는 제공 불가")
    }

    fun getAccessToken(code: String, state: String): String

    @Throws(JsonProcessingException::class)
    fun getMemberInfo(accessToken: String): SocialMemberCheckDto
}
