package com.grepp.somun.chat.repository

import com.grepp.somun.chat.entity.ChatMessageEntity
import com.grepp.somun.chat.repository.querydsl.ChatMessageRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository : JpaRepository<ChatMessageEntity, Long>, ChatMessageRepositoryCustom
