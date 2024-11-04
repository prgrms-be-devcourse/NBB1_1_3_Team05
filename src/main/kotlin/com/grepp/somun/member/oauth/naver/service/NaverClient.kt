package com.grepp.somun.member.oauth.naver.service

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.grepp.somun.member.entity.SocialProvider
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
import com.grepp.somun.member.oauth.common.service.SocialClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.*

/**
 * 네이버 OAuth 인증을 처리하고 사용자 정보를 조회하는 서비스 클래스
 * @author 김연수
 */
@Service
class NaverClient(
    @param:Value("\${spring.naver.client_id}") private val clientId: String,
    @param:Value("\${spring.naver.redirect_uri}") private val redirectURI: String,
    @param:Value("\${spring.naver.client_secret}") private val clientSecret: String,
    restTemplate: RestTemplate
) : SocialClient {
    private val restTemplate: RestTemplate = restTemplate
    private val log = LoggerFactory.getLogger(this.javaClass)!!


    /**
     * 인가코드로 네이버 API에 액세스 토큰 요청
     * @param code 인가코드
     * @param state 상태코드
     * @return 액세스 토큰
     */
    override fun getAccessToken(code: String, state: String): String {
        val reqUrl = "https://nid.naver.com/oauth2.0/token"

        //        RestTemplate restTemplate = new RestTemplate();
        val httpHeaders = HttpHeaders()

        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "authorization_code")
        params.add("client_id", clientId)
        params.add("client_secret", clientSecret)
        params.add("code", code)
        params.add("state", state)

        val tokenRequest: HttpEntity<MultiValueMap<String, String>> =
            HttpEntity<MultiValueMap<String, String>>(params, httpHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange<String>(
            reqUrl,
            HttpMethod.POST,
            tokenRequest,
            String::class.java
        )

        val asJsonObject: JsonObject = JsonParser
            .parseString(Objects.requireNonNull(response.body))
            .asJsonObject

        return asJsonObject.get("access_token").asString
    }

    /**
     * 액세스 토큰으로 네이버 사용자 정보 요청
     * @param accessToken
     * @return 이름, 이메일
     */
    override fun getMemberInfo(accessToken: String): SocialMemberCheckDto {
        val reqUrl = "https://openapi.naver.com/v1/nid/me"

        //        RestTemplate restTemplate = new RestTemplate();
        val httpHeaders = HttpHeaders()
        httpHeaders.add("Authorization", "Bearer $accessToken")

        val memberInfoRequest: HttpEntity<MultiValueMap<String, String>> =
            HttpEntity<MultiValueMap<String, String>>(httpHeaders)
        val response: ResponseEntity<String> = restTemplate.exchange<String>(
            reqUrl,
            HttpMethod.POST,
            memberInfoRequest,
            String::class.java
        )
        log.info("response: {} ", response)

        // JSON 파싱
        val jsonObject: JsonObject = JsonParser
            .parseString(Objects.requireNonNull(response.body!!))
            .asJsonObject
        val responseObject: JsonObject = jsonObject.getAsJsonObject("response")

        val email: String = responseObject.get("email").asString
        val providerId: String = responseObject.get("id").asString

        // NaverEntity 생성 후 반환
        return SocialMemberCheckDto.create(email, providerId, SocialProvider.NAVER)
    }
}