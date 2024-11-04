package com.grepp.somun.chat.service

import com.grepp.somun.chat.dto.ChatMessageDto
import com.grepp.somun.chat.dto.request.ChatMessageRequestDto
import com.grepp.somun.chat.dto.request.ChatRoomRequestDto
import com.grepp.somun.chat.dto.response.ChatMessageResponseDto
import com.grepp.somun.chat.dto.response.ChatRoomResponseDto

interface ChatService {

    // 새로운 채팅방 생성
    fun createChatRoom(email: String, performanceId: Long): ChatRoomResponseDto

    // 채팅 메시지 전송
    fun saveMessage(chatMessageDto: ChatMessageDto): ChatMessageResponseDto

    // 특정 채팅방의 모든 메시지 조회
    fun getMessagesByChatRoom(chatRoomId: Long): List<ChatMessageResponseDto>

    // 특정 유저의 채팅방 목록 조회
    fun getChatRoomsByMember(email: String, isManager: Boolean): List<ChatRoomResponseDto>
}
