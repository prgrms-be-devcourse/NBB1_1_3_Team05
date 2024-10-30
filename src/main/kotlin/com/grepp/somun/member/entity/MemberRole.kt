package com.grepp.somun.member.entity

enum class MemberRole(val description: String) {
    ROLE_USER("일반 사용자"),
    ROLE_PADMIN("공연 관리자"),
    ROLE_ADMIN("최종 관리자")
}
