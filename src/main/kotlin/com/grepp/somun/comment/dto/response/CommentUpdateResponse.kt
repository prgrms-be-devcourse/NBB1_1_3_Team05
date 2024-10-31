package com.grepp.somun.comment.dto.response

@JvmRecord
data class CommentUpdateResponse(val performanceId: Long) {
    companion object {
        fun from(performanceId: Long): CommentUpdateResponse {
            return CommentUpdateResponse(performanceId)
        } //정적 팩토리 메소드 적용해보기, 매개변수 1개니까 from
    }
}
