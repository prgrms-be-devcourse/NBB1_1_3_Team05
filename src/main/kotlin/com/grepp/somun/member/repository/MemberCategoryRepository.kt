package com.grepp.somun.member.repository

import com.grepp.somun.member.entity.MemberCategoryEntity
import com.grepp.somun.performance.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

@org.springframework.stereotype.Repository
interface MemberCategoryRepository : JpaRepository<MemberCategoryEntity, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT mc.category FROM MemberCategoryEntity mc WHERE mc.member.memberId = :memberId")
    fun findCategoriesByMemberId(memberId: Long): List<CategoryEntity>

    fun deleteByMemberMemberId(memberId: Long)
}
