package com.grepp.somun.chat.repository.querydsl

import com.grepp.somun.chat.entity.ChatRoomEntity
import java.util.Optional

interface ChatRoomRepositoryCustom {
    fun getChatRoomByPerformanceIdAndMemberId(performanceId: Long, memberId: Long): Optional<ChatRoomEntity>
    // QueryDSL을 이용한 특정 회원의 채팅방 목록 조회
    fun getChatRoomsByEmail(email: String, isManager: Boolean): List<ChatRoomEntity>
}
