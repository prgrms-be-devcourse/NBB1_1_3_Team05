package com.grepp.somun.global.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass // 직접적으로 테이블로 매핑되지 않으며, 이 클래스를 상속받는 엔티티들은 이 클래스에 정의된 필드와 매핑 정보를 자신의 테이블에 포함시킨다.
@EntityListeners(AuditingEntityListener::class) // 자동으로 생성시간 수정시간 기록해준다.
@SuperBuilder // 빌더패턴은 상속되지 않기때문 SuperBuilder를 사용해 상속받은 클래스에서도 빌더를 사용할 수 있게함.
@Getter
@NoArgsConstructor
class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private var createdAt: LocalDateTime? = null

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private var updatedAt: LocalDateTime? = null

    @Column(name = "deleted_at") //null 가능
    private var deletedAt: LocalDateTime? = null

    fun recordDeletedAt(deletedAt: LocalDateTime?) {
        this.deletedAt = deletedAt
    }
}
