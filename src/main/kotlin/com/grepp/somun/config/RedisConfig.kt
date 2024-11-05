package com.grepp.somun.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.grepp.somun.performance.dto.response.PerformanceListResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private var port: Int = 0

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    // 일반적인 조회수를 증감에 사용
    @Bean(name = ["longRedisTemplate"])
    fun longRedisTemplate(): RedisTemplate<String, Long> {
        val redisTemplate = RedisTemplate<String, Long>()
        redisTemplate.connectionFactory = redisConnectionFactory()

        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericToStringSerializer(Long::class.java)

        return redisTemplate
    }

    // PerformanceListResponse의 직렬,역직렬화를 위해 사용
    @Bean(name = ["performanceRedisTemplate"])
    fun jsonRedisTemplate(): RedisTemplate<String, PerformanceListResponse> {
        val redisTemplate = RedisTemplate<String, PerformanceListResponse>()
        redisTemplate.keySerializer = StringRedisSerializer()

        val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        val serializer = Jackson2JsonRedisSerializer<PerformanceListResponse>(objectMapper, PerformanceListResponse::class.java)
        redisTemplate.valueSerializer = serializer
        redisTemplate.connectionFactory = redisConnectionFactory()

        return redisTemplate
    }

    @Bean(name = ["queueRedisTemplate"])
    fun queueRedisTemplate(): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer() // 사용자 ID를 String으로 처리
        return redisTemplate
    }
}