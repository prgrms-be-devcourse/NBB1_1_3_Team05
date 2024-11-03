package com.grepp.somun.member.auth.service

import com.grepp.somun.member.entity.MemberEntity
import com.grepp.somun.member.entity.SocialProvider
import com.grepp.somun.member.repository.MemberRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class CustomUserDetailsService(
    private val memberRepository: MemberRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val member: MemberEntity = memberRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found: $email") }

        // 소셜 로그인 사용자인지 확인
        val isSocialLogin = member.provider !== SocialProvider.LOCAL

        // 권한 설정
        val grantedAuthority: GrantedAuthority = SimpleGrantedAuthority(member.role.name)

        // 소셜 사용자일 경우 비밀번호를 빈 문자열로 처리하고, 일반 사용자는 실제 비밀번호로 설정
        val password = if (isSocialLogin) "" else member.password
        return User(member.email, password, listOf(grantedAuthority))
    }
}
