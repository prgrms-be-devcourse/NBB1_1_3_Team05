package com.grepp.somun.comment.dto.response

import com.grepp.somun.comment.entity.CommentStatus
import lombok.Builder
import lombok.Getter

import java.time.LocalDateTime

/*
* 댓글 전체 조회
* */
@Getter
@Builder
class CommentReadDto {
    private val commentId: Long = 0
    private val memberId: Long = 0
    private val memberName: String? = null // 댓글에 사용자 이름을 표시하려면 name도 넘겨줘야하더라구요
    private val email: String? = null
    private val content: String? = null //엔티티 상에서는 comment
    private val createdAt: LocalDateTime? = null
    private val updatedAt: LocalDateTime? = null
    private val parentId: Long? = null
    private val commentStatus: CommentStatus? = null

    // 대댓글 리스트 추가
    @Builder.Default
    private val replies: List<CommentReadDto> = ArrayList()
} //이건 추후에 record로 바꾸겠슴다.

