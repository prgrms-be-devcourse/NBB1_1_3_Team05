package com.grepp.somun.chat.dto

data class ChatMessageDto(
    var chatRoomId: Long, // 방 번호
    var senderId: Long?, // 채팅을 보낸 사람
    var senderName: String?,
    var message: String?
) {
    // chatRoomId, senderName, message를 담은 정적 팩토리 메서드
    companion object {
        fun of(chatRoomId: Long, senderName: String, message: String): ChatMessageDto {
            return ChatMessageDto(
                chatRoomId = chatRoomId,
                senderId = null, // 필요한 경우 초기값 설정
                senderName = senderName,
                message = message
            )
        }
    }
}
