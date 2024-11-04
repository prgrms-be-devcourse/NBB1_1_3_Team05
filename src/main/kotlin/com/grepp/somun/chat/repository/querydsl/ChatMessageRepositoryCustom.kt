package com.grepp.somun.chat.repository.querydsl

import com.grepp.somun.chat.entity.ChatMessageEntity

interface ChatMessageRepositoryCustom {
    // QueryDSL을 이용한 특정 채팅방의 메시지 목록 조회
    fun getMessagesByChatRoomId(chatRoomId: Long): List<ChatMessageEntity>
    fun findLastMessageAndTimeAgoForChatRoom(chatRoomId: Long): Map<String, Any>?
}
