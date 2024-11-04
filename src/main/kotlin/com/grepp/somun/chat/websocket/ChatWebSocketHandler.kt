package com.grepp.somun.chat.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.somun.chat.dto.ChatListUpdateMessageDto
import com.grepp.somun.chat.dto.ChatMessageDto
import com.grepp.somun.chat.dto.response.ChatMessageResponseDto
import com.grepp.somun.chat.service.ChatService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatWebSocketHandler(
    private val mapper: ObjectMapper,
    private val chatService: ChatService
) : TextWebSocketHandler() {

    private val log = LoggerFactory.getLogger(ChatWebSocketHandler::class.java)
    private val chatListSessions = mutableSetOf<WebSocketSession>() // 채팅 목록용 WebSocket 세션
    private val sessions = mutableSetOf<WebSocketSession>() // 전체 세션
    private val chatRoomSessionMap = ConcurrentHashMap<Long, MutableSet<WebSocketSession>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("${session.id} 연결됨")

        // 세션 URI에 따라 채팅방 접속인지, 채팅 목록 접속인지 구분
        if (isChatListSession(session)) {
            chatListSessions.add(session)
        } else {
            val chatRoomId = getChatRoomIdFromSession(session)
            if (chatRoomId != null) {
                sessions.add(session)
                chatRoomSessionMap.computeIfAbsent(chatRoomId) { mutableSetOf() }.add(session)
            }
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        log.info("payload $payload")

        // 페이로드를 ChatMessageDto로 변환
        val chatMessageDto = mapper.readValue(payload, ChatMessageDto::class.java)
        val chatRoomId = getChatRoomIdFromSession(session)
        chatRoomId?.let {
            chatMessageDto.chatRoomId = it

            // 채팅 메시지 저장 및 전송
            val savedMessage = chatService.saveMessage(chatMessageDto)

            // 현재 채팅방에 연결된 세션에 메시지 전송
            val chatRoomSession = chatRoomSessionMap[it]
            chatRoomSession?.let { sessions ->
                sendMessageToChatRoom(savedMessage, sessions)

                // 채팅 목록에 실시간 업데이트 전송
                notifyChatListUpdate(it, savedMessage)

                // 불필요 세션 정리
                if (sessions.size >= 3) {
                    removeClosedSession(sessions)
                }
            }
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        log.info("${session.id} 연결 끊김")
        sessions.remove(session)
        chatListSessions.remove(session) // 채팅 목록 세션도 제거

        // 모든 채팅방에서 해당 세션 제거
        chatRoomSessionMap.values.forEach { it.remove(session) }
    }

    private fun notifyChatListUpdate(chatRoomId: Long, savedMessage: ChatMessageResponseDto) {
        // 채팅 목록에 전송할 업데이트 메시지 생성
        val updateMessage = ChatListUpdateMessageDto.of(
            chatRoomId,
            savedMessage.messageContent,
            savedMessage.unreadCount,
            savedMessage.sentAt
        )

        // 모든 채팅 목록 세션에 전송
        chatListSessions.forEach { sendMessage(it, updateMessage) }
    }

    private fun removeClosedSession(chatRoomSession: MutableSet<WebSocketSession>) {
        chatRoomSession.removeIf { !it.isOpen }
    }

    private fun sendMessageToChatRoom(chatMessageResponseDto: ChatMessageResponseDto, chatRoomSession: Set<WebSocketSession>) {
        chatRoomSession.parallelStream().forEach { sendMessage(it, chatMessageResponseDto) }
    }

    fun <T> sendMessage(session: WebSocketSession, message: T) {
        try {
            session.sendMessage(TextMessage(mapper.writeValueAsString(message)))
        } catch (e: IOException) {
            log.error(e.message, e)
        }
    }

    // 채팅 목록 세션인지 확인하는 메서드
    private fun isChatListSession(session: WebSocketSession): Boolean {
        return session.uri?.path?.contains("/chat-list") == true
    }

    // 세션에서 chatRoomId 가져오는 유틸리티 메서드
    private fun getChatRoomIdFromSession(session: WebSocketSession): Long? {
        val query = session.uri?.query
        return query?.takeIf { it.contains("chatRoomId=") }
            ?.split("chatRoomId=")?.get(1)?.split("&")?.get(0)?.toLongOrNull()
    }
}
