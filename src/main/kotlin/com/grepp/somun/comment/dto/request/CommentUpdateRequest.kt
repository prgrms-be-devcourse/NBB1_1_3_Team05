package com.grepp.somun.comment.dto.request


data class CommentUpdateRequest(val content: String = "") {
    companion object {
        fun from(content: String): CommentUpdateRequest {
            return CommentUpdateRequest(content)
        }
    }
}
