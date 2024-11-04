package com.grepp.somun.member.oauth.kakao.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.somun.member.entity.SocialProvider
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
import com.grepp.somun.member.oauth.common.service.SocialClient
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Slf4j
@Service
class KakaoClient(
    @param:Value("\${kakao.client_id}") private val clientId: String,
    @param:Value("\${kakao.redirect_uri}") private val redirectUri: String,
    @param:Value("\${kakao.client_secret}") private val clientSecret: String
) : SocialClient {
    override fun getAccessToken(code: String, state: String): String {
        throw UnsupportedOperationException("카카오에서는 지원하지 않습니다.")
    }

    /**
     * 클라이언트에서 인가 코드 받아서 access_token 반환
     * @param code
     * @return access_token
     * @throws JsonProcessingException
     */
    @Throws(JsonProcessingException::class)
    override fun getAccessToken(code: String): String {
        val webClient: WebClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=utf-8"
            )
            .build()
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("grant_type", "authorization_code")
        body.add("client_id", clientId)
        body.add("redirect_uri", redirectUri)
        body.add("code", code)
        body.add("client_secret", clientSecret)
        val responseBody: String? = webClient.post()
            .uri("/oauth/token")
            .bodyValue(body)
            .retrieve()
            .onStatus(
                { obj: HttpStatusCode -> obj.is4xxClientError },
                {
                    Mono.error<Throwable?>(
                        RuntimeException("Invalid request")
                    )
                })
            .onStatus(
                { obj: HttpStatusCode -> obj.is5xxServerError },
                {
                    Mono.error<Throwable?>(
                        RuntimeException("Server error")
                    )
                })
            .bodyToMono<String>(String::class.java)
            .block()
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)
        return jsonNode["access_token"].asText()
    }

    /**
     * accessToken을 이용하여 카카오에서 유저의 정보(이메일, id)를 가져온다.
     * @param accessToken
     * @return email, id
     * @throws JsonProcessingException
     */
    @Throws(JsonProcessingException::class)
    override fun getMemberInfo(accessToken: String): SocialMemberCheckDto {
        val webClient: WebClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                "application/x-www-form-urlencoded;charset=utf-8"
            )
            .build()
        val responseBody: String? = webClient.post()
            .uri("/v2/user/me")
            .retrieve()
            .onStatus(
                { obj: HttpStatusCode -> obj.is4xxClientError },
                {
                    Mono.error<Throwable?>(
                        RuntimeException("Invalid request")
                    )
                })
            .onStatus(
                { obj: HttpStatusCode -> obj.is5xxServerError },
                {
                    Mono.error<Throwable?>(
                        RuntimeException("Server error")
                    )
                })
            .bodyToMono<String>(String::class.java)
            .block()
        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)
        val email = jsonNode["kakao_account"]["email"].asText()
        val providerId = jsonNode["id"].asText()
        return SocialMemberCheckDto.create(email, providerId, SocialProvider.KAKAO)
    }
}
