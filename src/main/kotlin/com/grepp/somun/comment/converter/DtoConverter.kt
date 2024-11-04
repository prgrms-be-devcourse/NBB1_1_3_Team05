package com.grepp.somun.comment.converter

import com.grepp.somun.comment.dto.response.CommentReadDto
import com.grepp.somun.comment.entity.CommentEntity


object DtoConverter {
    fun fromCommentEntity(commentEntity: CommentEntity): CommentReadDto {
        return CommentReadDto(
            commentId = commentEntity.commentId ?: throw IllegalArgumentException("Comment ID cannot be null"),
            memberId = commentEntity.member.memberId ?: throw IllegalArgumentException("Member ID cannot be null"),
            content = commentEntity.content,
            createdAt = commentEntity.createdAt,
            updatedAt = commentEntity.updatedAt,
            memberName = commentEntity.member.name,
            parentId = commentEntity.parentComment?.commentId, // 부모 댓글 ID 설정
            commentStatus = commentEntity.commentStatus,
            replies = commentEntity.replies.map { fromCommentEntity(it) }, // 대댓글 리스트 매핑
            email = commentEntity.member.email
        )
    }
}
