package com.grepp.somun.comment.service

import com.grepp.somun.comment.converter.DtoConverter
import com.grepp.somun.comment.dto.request.CommentCreateRequest
import com.grepp.somun.comment.dto.request.CommentUpdateRequest
import com.grepp.somun.comment.dto.response.CommentCreateResponse
import com.grepp.somun.comment.dto.response.CommentDeleteResponse
import com.grepp.somun.comment.dto.response.CommentReadDto
import com.grepp.somun.comment.dto.response.CommentUpdateResponse
import com.grepp.somun.comment.entity.CommentEntity
import com.grepp.somun.comment.entity.CommentStatus
import com.grepp.somun.comment.repository.CommentRepository
import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.repository.MemberRepository
import com.grepp.somun.performance.repository.PerformanceRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CommentServiceImpl(
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
    private val performanceRepository: PerformanceRepository
) : CommentService {

    private val log = LoggerFactory.getLogger(CommentServiceImpl::class.java)

    override fun getAllComment(performanceId: Long, pageable: Pageable): List<CommentReadDto> {
        val commentEntityList = commentRepository.findParentCommentsByPerformanceId(performanceId, pageable)

        if (commentEntityList.isEmpty()) {
            throw GeneralException(ErrorStatus.COMMENT_NOT_FOUND)
        }

        return commentEntityList.mapNotNull { DtoConverter.fromCommentEntity(it) }
    }

    override fun createComment(performanceId: Long, commentCreateRequest: CommentCreateRequest): CommentCreateResponse {
        val email = SecurityContextHolder.getContext().authentication.name

        val performance = performanceRepository.findById(performanceId)
            .orElseThrow { GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND) }

        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR) }

        val parentId = commentCreateRequest.parentId
        log.info("Received parentId: {}", parentId)  // parentId 확인 로그

        val parentComment = if (parentId != null) {
            commentRepository.findById(parentId).orElseThrow {
                log.info("Comment with parentId {} not found", parentId)  // parentId로 댓글 조회 실패 시 로그
                GeneralException(ErrorStatus.COMMENT_NOT_FOUND)
            }.also {
                log.info("Successfully retrieved parentComment with id: {}", it.commentId)  // parentComment 조회 성공 시 로그
            }
        } else {
            null
        }

        val commentEntity = CommentEntity(
            content = commentCreateRequest.content,
            parentComment = parentComment,
            commentStatus = CommentStatus.ACTIVE,
            performance = performance,
            member = member
        )

        val savedComment = commentRepository.save(commentEntity)
        val commentId = savedComment.commentId ?: throw GeneralException(ErrorStatus.COMMENT_NOT_FOUND)

        return CommentCreateResponse.of(
            commentId,  // commentId 전달
            savedComment.content,
            savedComment.performance.performanceId ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)
        )
    }



    override fun updateComment(commentId: Long, commentUpdateRequest: CommentUpdateRequest): CommentUpdateResponse {
        val commentEntity = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        val email = SecurityContextHolder.getContext().authentication.name
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        log.info("Authenticated user email {}", email)
        log.info("Authenticated Member: id = {}, email = {}", member.memberId, member.email)
        log.info("Comment Author Member: id = {}, email = {}", commentEntity.member.memberId, commentEntity.member.email)

        if (commentEntity.member != member) {
            throw GeneralException(ErrorStatus._COMMENT_NOT_AUTHORIZED)
        }

        commentEntity.updateContent(commentUpdateRequest.content)
        commentRepository.save(commentEntity)

        // Use safe call or ensure the value is not null
        val performanceId = commentEntity.performance.performanceId ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)

        return CommentUpdateResponse.from(performanceId)
    }

    override fun deleteComment(commentId: Long): CommentDeleteResponse {
        val commentEntity = commentRepository.findById(commentId)
            .orElseThrow { GeneralException(ErrorStatus.COMMENT_NOT_FOUND) }

        val email = SecurityContextHolder.getContext().authentication.name
        val member = memberRepository.findByEmail(email)
            .orElseThrow { GeneralException(ErrorStatus.MEMBER_NOT_FOUND) }

        if (commentEntity.member != member) {
            throw GeneralException(ErrorStatus._COMMENT_NOT_AUTHORIZED)
        }

        commentEntity.recordDeletedAt(LocalDateTime.now())
        commentEntity.changeCommentStatus(CommentStatus.DELETED)
        commentRepository.save(commentEntity)

        // Use safe call or ensure the value is not null
        val performanceId = commentEntity.performance.performanceId ?: throw GeneralException(ErrorStatus.PERFORMANCE_NOT_FOUND)

        return CommentDeleteResponse.of(
            performanceId,
            commentEntity.commentStatus
        )
    }
}
