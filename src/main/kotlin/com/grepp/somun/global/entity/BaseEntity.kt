package com.grepp.somun.global.entity


import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime


@MappedSuperclass // 직접적으로 테이블로 매핑되지 않음
@EntityListeners(AuditingEntityListener::class) // 자동으로 생성시간, 수정시간 기록

open class BaseEntity(//코틀린에선 기본적으로 모든 클래스가 final이다. open을 붙여줘야 상속가능
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,

    @Column(name = "deleted_at") // null 가능
    var deletedAt: LocalDateTime? = null
) {

    fun recordDeletedAt(deletedAt: LocalDateTime) {
        this.deletedAt = deletedAt
    }
}
