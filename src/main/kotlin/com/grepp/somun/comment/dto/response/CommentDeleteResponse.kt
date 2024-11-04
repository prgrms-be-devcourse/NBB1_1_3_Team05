package com.grepp.somun.comment.dto.response

import com.grepp.somun.comment.entity.CommentStatus


data class CommentDeleteResponse(val performanceId: Long, val commentStatus: CommentStatus) {

    companion object {
        fun of(performanceId: Long, commentStatus: CommentStatus): CommentDeleteResponse {
            return CommentDeleteResponse(performanceId, commentStatus)
        }
    }
}
