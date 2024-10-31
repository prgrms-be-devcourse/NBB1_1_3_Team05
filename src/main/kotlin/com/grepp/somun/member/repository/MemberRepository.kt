package com.grepp.somun.member.repository

import com.grepp.somun.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberRepository : JpaRepository<MemberEntity?, Long?> {
    // 이메일 중복 확인
    fun existsByEmail(email: String?): Boolean

    // 닉네임 중복 확인
    fun existsByName(name: String?): Boolean

    // 이메일로 사용자 조회
    fun findByEmail(email: String?): Optional<MemberEntity?>?
}
