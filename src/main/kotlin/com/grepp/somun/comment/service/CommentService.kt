package com.grepp.somun.comment.service

import com.grepp.somun.comment.dto.request.CommentCreateRequest
import com.grepp.somun.comment.dto.request.CommentUpdateRequest
import com.grepp.somun.comment.dto.response.CommentCreateResponse
import com.grepp.somun.comment.dto.response.CommentDeleteResponse
import com.grepp.somun.comment.dto.response.CommentReadDto
import com.grepp.somun.comment.dto.response.CommentUpdateResponse
import org.springframework.data.domain.Pageable






interface CommentService {
    fun getAllComment(performanceId: Long, pageable: Pageable?): List<CommentReadDto> // 댓글 전체조회

    fun createComment(performanceId: Long, commentCreateRequest: CommentCreateRequest?): CommentCreateResponse

    fun updateComment(commentId: Long, commentUpdateRequest: CommentUpdateRequest?): CommentUpdateResponse

    fun deleteComment(commentId: Long): CommentDeleteResponse
}