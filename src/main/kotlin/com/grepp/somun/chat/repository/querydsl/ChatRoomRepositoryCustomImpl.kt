package com.grepp.somun.chat.repository.querydsl

import com.grepp.somun.chat.entity.ChatRoomEntity
import com.grepp.somun.chat.entity.QChatRoomEntity
import com.grepp.somun.member.entity.QMemberEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

import java.util.Optional

@Repository
class ChatRoomRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : ChatRoomRepositoryCustom {

    override fun getChatRoomByPerformanceIdAndMemberId(performanceId: Long, memberId: Long): Optional<ChatRoomEntity> {
        val chatRoomEntity = QChatRoomEntity.chatRoomEntity

        val chatRoom = queryFactory
            .selectFrom(chatRoomEntity)
            .where(
                chatRoomEntity.performance.performanceId.eq(performanceId),
                chatRoomEntity.member.memberId.eq(memberId)
            )
            .fetchOne()

        return Optional.ofNullable(chatRoom)
    }

    override fun getChatRoomsByEmail(email: String, isManager: Boolean):List<ChatRoomEntity> {
        val chatRoomEntity = QChatRoomEntity.chatRoomEntity
        val memberEntity = QMemberEntity.memberEntity

        return queryFactory.selectFrom(chatRoomEntity)
            .join(if (isManager) chatRoomEntity.manager else chatRoomEntity.member, memberEntity) // manager 또는 member로 조인
            .where(memberEntity.email.eq(email)) // 이메일 조건으로 필터링
            .fetch()
    }
}
