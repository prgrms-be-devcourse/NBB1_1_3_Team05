package com.grepp.somun.chat.service

import com.grepp.somun.chat.dto.ChatMessageDto
import com.grepp.somun.chat.dto.response.ChatMessageResponseDto
import com.grepp.somun.chat.dto.response.ChatRoomResponseDto
import com.grepp.somun.chat.entity.ChatMessageEntity
import com.grepp.somun.chat.entity.ChatRoomEntity
import com.grepp.somun.chat.repository.ChatMessageRepository
import com.grepp.somun.chat.repository.ChatRoomRepository
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.performance.entity.PerformanceEntity
import com.grepp.somun.performance.repository.PerformanceRepository
import org.springframework.stereotype.Service

@Service
class ChatServiceImpl(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val memberRepository: MemberRepository,
    private val performanceRepository: PerformanceRepository
) : ChatService {

    override fun createChatRoom(email: String, performanceId: Long): ChatRoomResponseDto {
        val performanceEntity = performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }
        val memberEntity = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }
        val managerEntity = memberRepository.findById(performanceEntity.member!!.memberId!!)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        // 채팅방 중복 확인
        val existingChatRoom = chatRoomRepository.getChatRoomByPerformanceIdAndMemberId(
            performanceEntity.performanceId!!,
            memberEntity.memberId!!
        )

        if (existingChatRoom.isPresent) {
            val existingRoom = existingChatRoom.get()
            return ChatRoomResponseDto.fromEntity(existingRoom, null, null)
        }

        // 새 채팅방 생성
        val chatRoomEntity = ChatRoomEntity(
            performance = performanceEntity,
            member = memberEntity,
            manager = managerEntity
        )

        val savedChatRoom = chatRoomRepository.save(chatRoomEntity)

        // ChatRoomEntity -> ChatRoomResponseDto 변환 후 반환
        return ChatRoomResponseDto.fromEntity(savedChatRoom, null, null)
    }

    override fun saveMessage(chatMessageDto: ChatMessageDto): ChatMessageResponseDto {
        val chatRoomEntity = chatRoomRepository.findById(chatMessageDto.chatRoomId!!)
            .orElseThrow { GeneralException(ErrorStatus.CHAT_ROOM_NOT_FOUND) }
        val senderEntity = memberRepository.findByName(chatMessageDto.senderName!!)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        val message = ChatMessageEntity(
            chatRoomEntity = chatRoomEntity,
            sender = senderEntity,
            messageContent = chatMessageDto.message!!,
            isRead = false
        )

        val savedMessage = chatMessageRepository.save(message)

        // ChatMessageEntity -> ChatMessageResponseDto 변환 후 반환
        return ChatMessageResponseDto.fromEntity(savedMessage)
    }

    override fun getMessagesByChatRoom(chatRoomId: Long): List<ChatMessageResponseDto> {
        // 엔티티 목록을 DTO로 변환
        return chatMessageRepository.getMessagesByChatRoomId(chatRoomId)
            .map { ChatMessageResponseDto.fromEntity(it) }
    }

    override fun getChatRoomsByMember(email: String, isManager: Boolean): List<ChatRoomResponseDto> {
        // 이메일로 사용자가 참여한 채팅방 목록을 조회
        return chatRoomRepository.getChatRoomsByEmail(email, isManager)
            .map { chatRoomEntity ->
                // 각 채팅방의 마지막 메시지와 시간 경과 조회
                val lastMessageInfo = chatMessageRepository.findLastMessageAndTimeAgoForChatRoom(chatRoomEntity.chatRoomId!!)

                // lastMessage와 timeAgo 값을 추출 (null일 경우 기본값 설정)
                val lastMessage = lastMessageInfo?.get("lastMessage") as? String ?: ""
                val timeAgo = lastMessageInfo?.get("timeAgo") as? String ?: ""

                // ChatRoomEntity와 마지막 메시지 정보를 사용해 ChatRoomResponseDto 생성
                ChatRoomResponseDto.fromEntity(chatRoomEntity, lastMessage, timeAgo)
            }
    }
}
