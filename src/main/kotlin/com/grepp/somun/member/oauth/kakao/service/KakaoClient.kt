package com.grepp.somun.member.oauth.kakao.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.somun.member.entity.SocialProvider
import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
import com.grepp.somun.member.oauth.common.service.SocialClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class KakaoClient(
    @Value("\${spring.kakao.client_id}") private val clientId: String,
    @Value("\${spring.kakao.redirect_uri}") private val redirectUri: String,
    @Value("\${spring.kakao.client_secret}") private val clientSecret: String
) : SocialClient {

    override fun getAccessToken(code: String, state: String): String {
        throw UnsupportedOperationException("카카오에서는 지원하지 않습니다.")
    }

    @Throws(JsonProcessingException::class)
    override fun getAccessToken(code: String): String {
        val webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            .build()

        val body: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("client_id", clientId)
            add("redirect_uri", redirectUri)
            add("code", code)
            add("client_secret", clientSecret)
        }

        val responseBody = webClient.post()
            .uri("/oauth/token")
            .bodyValue(body)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { Mono.error(RuntimeException("Invalid request")) }
            .onStatus(HttpStatusCode::is5xxServerError) { Mono.error(RuntimeException("Server error")) }
            .bodyToMono(String::class.java)
            .block() ?: throw RuntimeException("Response body is null")

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)
        return jsonNode["access_token"].asText()
    }

    @Throws(JsonProcessingException::class)
    override fun getMemberInfo(accessToken: String): SocialMemberCheckDto {
        val webClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")
            .build()

        val responseBody = webClient.post()
            .uri("/v2/user/me")
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { Mono.error(RuntimeException("Invalid request")) }
            .onStatus(HttpStatusCode::is5xxServerError) { Mono.error(RuntimeException("Server error")) }
            .bodyToMono(String::class.java)
            .block() ?: throw RuntimeException("Response body is null")

        val objectMapper = ObjectMapper()
        val jsonNode = objectMapper.readTree(responseBody)

        val email = jsonNode["kakao_account"]["email"].asText()
        val providerId = jsonNode["id"].asText()

        return SocialMemberCheckDto.create(email, providerId, SocialProvider.KAKAO)
    }
}
