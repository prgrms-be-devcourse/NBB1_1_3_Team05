package com.grepp.somun.chat.repository.querydsl

import com.grepp.somun.chat.entity.ChatMessageEntity
import com.grepp.somun.chat.entity.QChatMessageEntity
import com.grepp.somun.chat.util.TimeAgoUtil
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ChatMessageRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : ChatMessageRepositoryCustom {

    override fun getMessagesByChatRoomId(chatRoomId: Long): List<ChatMessageEntity> {
        val chatMessageEntity = QChatMessageEntity.chatMessageEntity

        return queryFactory.selectFrom(chatMessageEntity)
            .where(chatMessageEntity.chatRoomEntity.chatRoomId.eq(chatRoomId))
            .fetch()
    }

    override fun findLastMessageAndTimeAgoForChatRoom(chatRoomId: Long): Map<String, Any>? {
        val chatMessage = QChatMessageEntity.chatMessageEntity

        // 마지막 메시지 내용과 전송 시간만 조회
        val result = queryFactory
            .select(chatMessage.messageContent, chatMessage.sentAt)
            .from(chatMessage)
            .where(chatMessage.chatRoomEntity.chatRoomId.eq(chatRoomId))
            .orderBy(chatMessage.sentAt.desc())
            .fetchFirst()

        // 조회 결과가 없으면 null 반환
        if (result == null) {
            return null
        }

        // 결과를 Map에 담기
        val map = mutableMapOf<String, Any>()
        map["lastMessage"] = result.get(chatMessage.messageContent)!!

        // 시간 경과를 계산하여 timeAgo 값 추가
        val sentAt: LocalDateTime = result.get(chatMessage.sentAt)!!
        map["timeAgo"] = TimeAgoUtil.getElapsedTime(sentAt)

        return map
    }
}
