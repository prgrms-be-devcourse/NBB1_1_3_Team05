package com.grepp.somun.chat.dto

import com.grepp.somun.chat.util.TimeAgoUtil
import java.time.LocalDateTime

// 채팅 목록 업데이트 메시지 DTO
data class ChatListUpdateMessageDto(
    val chatRoomId: Long,
    val lastMessage: String,
    val unreadCount: Int,
    val timeAgo: String // 마지막 채팅 날짜 필드 추가
) {
    companion object {
        // 정적 팩토리 메서드
        fun of(chatRoomId: Long, lastMessage: String, unreadCount: Int, sentAt: LocalDateTime): ChatListUpdateMessageDto {
            return ChatListUpdateMessageDto(
                chatRoomId = chatRoomId,
                lastMessage = lastMessage,
                unreadCount = unreadCount,
                timeAgo = TimeAgoUtil.getElapsedTime(sentAt)
            )
        }
    }
}
