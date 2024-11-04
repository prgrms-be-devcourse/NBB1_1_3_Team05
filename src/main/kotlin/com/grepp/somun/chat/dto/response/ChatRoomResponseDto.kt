package com.grepp.somun.chat.dto.response

import com.grepp.somun.chat.entity.ChatRoomEntity

data class ChatRoomResponseDto(
    val chatRoomId: Long,
    val performanceId: Long,
    val memberId: Long,
    val managerId: Long,
    val title: String,       // 공연 이름
    val imageUrl: String,    // 공연 이미지(url)
    val lastMessage: String?, // 마지막 메세지
    val timeAgo: String?      // 마지막 메세지를 기준으로 지난 시간
) {
    companion object {
        // fromEntity 메서드 - lastMessage와 timeAgo를 인자로 받아서 사용
        fun fromEntity(chatRoomEntity: ChatRoomEntity, lastMessage: String?, timeAgo: String?): ChatRoomResponseDto {
            return ChatRoomResponseDto(
                chatRoomId = chatRoomEntity.chatRoomId!!,
                performanceId = chatRoomEntity.performance.performanceId!!,
                memberId = chatRoomEntity.member.memberId!!,
                managerId = chatRoomEntity.manager.memberId!!,
                title = chatRoomEntity.performance.title,
                imageUrl = chatRoomEntity.performance.imageUrl!!,
                lastMessage = lastMessage,
                timeAgo = timeAgo
            )
        }
    }
}
