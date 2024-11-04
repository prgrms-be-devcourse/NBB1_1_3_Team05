package com.grepp.somun.comment.controller



import com.grepp.somun.comment.dto.request.CommentCreateRequest
import com.grepp.somun.comment.dto.request.CommentUpdateRequest
import com.grepp.somun.comment.dto.response.CommentCreateResponse
import com.grepp.somun.comment.dto.response.CommentDeleteResponse
import com.grepp.somun.comment.dto.response.CommentReadDto
import com.grepp.somun.comment.dto.response.CommentUpdateResponse
import com.grepp.somun.comment.service.CommentService
import com.grepp.somun.global.apiResponse.ApiResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/comments")
class CommentController(
    private val commentService: CommentService
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CommentController::class.java)
    }

    /**
     * 전체 댓글 조회
     */
    @GetMapping("/{performanceId}")
    fun getComment(
        @PathVariable("performanceId") performanceId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<List<CommentReadDto>>> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by("commentId").ascending())
        val commentReadDtos = commentService.getAllComment(performanceId, pageable)
        return ApiResponse.onSuccess(commentReadDtos)
    }

    /**
     * 댓글 생성
     */
    @PostMapping("/{performanceId}")
    fun createComment(
        @PathVariable("performanceId") performanceId: Long,
        @RequestBody commentCreateRequest: CommentCreateRequest
    ): ResponseEntity<ApiResponse<CommentCreateResponse>> {
        logger.info("서비스 호출 전 : {}", performanceId)
        val commentCreateResponse = commentService.createComment(performanceId, commentCreateRequest)
        logger.info("Successfully created a comment for performanceId: {}", performanceId)
        return ApiResponse.onSuccess(HttpStatus.CREATED, "COMMENT201", "댓글 작성 성공", commentCreateResponse)
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/{commentId}")
    fun updateComment(
        @PathVariable("commentId") commentId: Long,
        @RequestBody commentUpdateRequest: CommentUpdateRequest
    ): ResponseEntity<ApiResponse<CommentUpdateResponse>> {
        val commentUpdateResponse = commentService.updateComment(commentId, commentUpdateRequest)
        return ApiResponse.onSuccess(commentUpdateResponse)
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable("commentId") commentId: Long
    ): ResponseEntity<ApiResponse<CommentDeleteResponse>> {
        val commentDeleteResponse = commentService.deleteComment(commentId)
        return ApiResponse.onSuccess(commentDeleteResponse)
    }
}
