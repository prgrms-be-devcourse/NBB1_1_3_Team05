package com.grepp.somun.chat.dto.response

import com.grepp.somun.chat.entity.ChatMessageEntity
import com.grepp.somun.chat.util.TimeAgoUtil
import java.time.LocalDateTime

data class ChatMessageResponseDto(
    val messageId: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val senderName: String,
    val messageContent: String,
    val sentAt: LocalDateTime,
    val formattedSentTime: String, // 채팅 화면에서 채팅 시간 설정
    val unreadCount: Int
) {
    companion object {
        // fromEntity 메서드
        fun fromEntity(chatMessageEntity: ChatMessageEntity): ChatMessageResponseDto {
            return ChatMessageResponseDto(
                messageId = chatMessageEntity.messageId!!,
                chatRoomId = chatMessageEntity.chatRoomEntity.chatRoomId!!,
                senderId = chatMessageEntity.sender.memberId!!,
                senderName = chatMessageEntity.sender.name,
                messageContent = chatMessageEntity.messageContent,
                sentAt = chatMessageEntity.sentAt,
                formattedSentTime = TimeAgoUtil.formatToAmPm(chatMessageEntity.sentAt),
                unreadCount = 10
            )
        }
    }
}
