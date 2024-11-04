package com.grepp.somun.comment.dto.request

import lombok.Builder



data class CommentCreateRequest(
    val content: String,
    val parentId: Long
) {
    companion object {
        fun of(content: String?, parentId: Long?): CommentCreateRequest {
            return CommentCreateRequest(
                content = content ?: "",
                parentId = parentId ?: 0L // 기본값을 지정하여 null 처리
            )
        }
    }
}

