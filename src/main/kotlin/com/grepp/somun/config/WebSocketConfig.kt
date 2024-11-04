package com.grepp.somun.config

import com.grepp.somun.chat.websocket.ChatWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

/**
 * 채팅용 웹 소켓 설정 파일
 *
 * @author ycjung
 */
@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val chatWebSocketHandler: ChatWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat", "/ws/chat/chat-list")
            .setAllowedOrigins("*")  // CORS 설정, 모든 도메인 허용
            .addInterceptors(HttpSessionHandshakeInterceptor())  // WebSocket 핸드셰이크 시 세션 정보 유지
    }
}
