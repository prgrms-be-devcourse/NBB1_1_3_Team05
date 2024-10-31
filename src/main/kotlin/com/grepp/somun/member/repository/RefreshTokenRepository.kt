package com.grepp.somun.member.repository

import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity?, Long?> {
    fun findByMember(member: MemberEntity?): Optional<RefreshTokenEntity?>?

    @Transactional
    fun deleteAllByMember(member: MemberEntity?)
}
