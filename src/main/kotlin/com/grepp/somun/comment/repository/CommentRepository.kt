package com.grepp.somun.comment.repository

import com.grepp.somun.comment.entity.CommentEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface CommentRepository : JpaRepository<CommentEntity, Long> {

    @Query("SELECT c FROM CommentEntity c WHERE c.performance.performanceId = :performanceId AND c.parentComment IS NULL")
    fun findParentCommentsByPerformanceId(
        @Param("performanceId") performanceId: Long,
        pageable: Pageable
    ): List<CommentEntity>
}