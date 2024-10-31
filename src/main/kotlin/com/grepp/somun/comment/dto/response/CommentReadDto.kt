package com.grepp.somun.comment.dto.response

import com.grepp.somun.comment.entity.CommentStatus
import java.time.LocalDateTime

/**
 * 댓글 전체 조회 DTO
 */
data class CommentReadDto(
    val commentId: Long,
    val memberId: Long,
    val memberName: String?, // 댓글에 사용자 이름 표시
    val email: String?,
    val content: String?, // 엔티티 상에서는 comment
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val parentId: Long?,
    val commentStatus: CommentStatus?,
    val replies: List<CommentReadDto> = emptyList() // 기본값으로 빈 리스트 설정
)
