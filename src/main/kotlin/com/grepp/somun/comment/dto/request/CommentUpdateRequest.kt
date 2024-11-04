package com.grepp.somun.comment.dto.request


data class CommentUpdateRequest(val content: String) {
    companion object {
        fun from(content: String): CommentUpdateRequest {
            return CommentUpdateRequest(content)
        } //정적 팩토리 메소드 적용해보기, 매개변수 1개니까 from
    }
}
