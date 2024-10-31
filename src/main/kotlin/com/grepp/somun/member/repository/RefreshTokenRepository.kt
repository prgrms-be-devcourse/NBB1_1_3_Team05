package com.grepp.somun.member.repository

import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

@org.springframework.stereotype.Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity?, Long?> {
    fun findByMember(member: MemberEntity): java.util.Optional<RefreshTokenEntity>

    @org.springframework.transaction.annotation.Transactional
    fun deleteAllByMember(member: MemberEntity)
}