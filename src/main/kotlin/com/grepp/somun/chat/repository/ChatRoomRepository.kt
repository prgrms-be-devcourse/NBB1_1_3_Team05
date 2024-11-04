package com.grepp.somun.chat.repository

import com.grepp.somun.chat.entity.ChatRoomEntity
import com.grepp.somun.chat.repository.querydsl.ChatRoomRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository : JpaRepository<ChatRoomEntity, Long>, ChatRoomRepositoryCustom
