package com.grepp.somun.comment.entity



import com.grepp.somun.global.entity.BaseEntity
import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.performance.entity.PerformanceEntity
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize


@Entity
@Table(name = "comment")
open class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    val commentId: Long? = null, // commentId를 nullable로 설정

    @Column(name = "comment", unique = true, nullable = false)
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = true)
    var commentStatus: CommentStatus = CommentStatus.ACTIVE, // 기본값 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    val performance: PerformanceEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @OneToMany(mappedBy = "parentComment")
    @BatchSize(size = 10)
    val replies: MutableList<CommentEntity> = ArrayList(), // 부모 댓글이 가진 대댓글 리스트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parentComment: CommentEntity? = null // 부모 댓글 참조, null 허용
) : BaseEntity() {

    // content 업데이트
    fun updateContent(newContent: String) {
        this.content = newContent
    }

    // commentStatus 변경
    fun changeCommentStatus(commentStatus: CommentStatus) {
        this.commentStatus = commentStatus
    }
}
