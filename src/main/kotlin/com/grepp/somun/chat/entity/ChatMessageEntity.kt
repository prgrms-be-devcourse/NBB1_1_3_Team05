package com.grepp.somun.chat.entity

import com.grepp.somun.member.entity.MemberEntity
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "chat_message")
@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
class ChatMessageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    val messageId: Long? = null,  // 메시지의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    val chatRoomEntity: ChatRoomEntity,  // 채팅방과 연결 (외래 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: MemberEntity,  // 메시지를 보낸 사람 (외래 키)

    @Column(name = "message_content", nullable = false)
    val messageContent: String,  // 메시지 내용

    @Column(name = "sent_at", nullable = false, updatable = false)
    @CreatedDate
    val sentAt: LocalDateTime = LocalDateTime.now(),  // 메시지가 전송된 시간 (자동 설정)

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false  // 메시지 읽음 여부 (기본값: false)
)
