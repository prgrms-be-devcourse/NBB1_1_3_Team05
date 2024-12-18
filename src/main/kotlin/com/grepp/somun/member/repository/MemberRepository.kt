package com.grepp.somun.member.repository

import com.grepp.somun.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

@org.springframework.stereotype.Repository
interface MemberRepository : JpaRepository<MemberEntity, Long> {
    // 이메일 중복 확인
    fun existsByEmail(email: String): Boolean

    // 닉네임 중복 확인
    fun existsByName(name: String): Boolean

    // 이메일로 사용자 조회
    fun findByEmail(email: String): java.util.Optional<MemberEntity>

    // 닉네임으로 사용자 조회
    fun findByName(email: String): java.util.Optional<MemberEntity>
}