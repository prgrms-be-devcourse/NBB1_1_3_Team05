package com.grepp.somun.config

import com.grepp.somun.member.auth.JwtAccessDeniedHandler
import com.grepp.somun.member.auth.JwtAuthenticationEntryPoint
import com.grepp.somun.member.auth.JwtFilter
import com.grepp.somun.member.auth.JwtTokenProvider
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val jwtAccessDeniedHandler: JwtAccessDeniedHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.disable() }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/members/**").permitAll()
                    .requestMatchers("/api/v1/members/categories/**").authenticated()
                    .anyRequest().permitAll()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }


    // CORS 설정을 위한 빈
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000") // 허용할 프론트엔드 도메인
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH") // 허용할 HTTP 메서드
        configuration.allowedHeaders = listOf("*") // 모든 헤더 허용
        configuration.allowCredentials = true // 자격 증명 허용 (세션 관리에 필요)

        // CORS 설정 적용
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}