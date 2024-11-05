package com.grepp.somun.chat.controller

import com.grepp.somun.chat.dto.ChatMessageDto
import com.grepp.somun.chat.dto.request.ChatMessageRequestDto
import com.grepp.somun.chat.dto.response.ChatMessageResponseDto
import com.grepp.somun.chat.dto.response.ChatRoomResponseDto
import com.grepp.somun.chat.service.ChatService
import com.grepp.somun.global.apiResponse.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/chats/room")
class ChatController(
    private val chatService: ChatService
) {

    // 새로운 채팅방 생성
    @PostMapping("/{performanceId}")
    fun createChatRoom(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable performanceId: Long
    ): ResponseEntity<ApiResponse<ChatRoomResponseDto>> {
        return ApiResponse.onSuccess(chatService.createChatRoom(userDetails.username, performanceId))
    }

    // 채팅 메시지 전송
    @PostMapping("/{chatRoomId}/message")
    fun sendMessage(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable chatRoomId: Long,
        @RequestBody chatMessageRequestDto: ChatMessageRequestDto
    ): ResponseEntity<ApiResponse<ChatMessageResponseDto>> {
        val chatMessageDto = ChatMessageDto.of(
            chatRoomId = chatRoomId,
            senderName = userDetails.username,
            message = chatMessageRequestDto.messageContent
        )
        return ApiResponse.onSuccess(chatService.saveMessage(chatMessageDto))
    }

    // 특정 채팅방의 메시지 목록 조회
    @GetMapping("/{chatRoomId}/messages")
    fun getMessages(@PathVariable chatRoomId: Long): ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> {
        return ApiResponse.onSuccess(chatService.getMessagesByChatRoom(chatRoomId))
    }

    // 사용자가 참여한 채팅방 목록 조회
    @GetMapping("/my")
    fun getChatRooms(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam(name = "isManager", defaultValue = "false") isManager: Boolean
    ): ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> {
        return ApiResponse.onSuccess(chatService.getChatRoomsByMember(userDetails.username, isManager))
    }
}
