//package com.grepp.somun.member.oauth.common.controller
//
//import com.fasterxml.jackson.core.JsonProcessingException
//import com.grepp.somun.global.apiResponse.exception.ErrorStatus
//import com.grepp.somun.global.apiResponse.exception.GeneralException
//import com.grepp.somun.member.auth.dto.TokenResponseDTO
//import com.grepp.somun.member.dto.request.SocialRegisterRequest
//import com.grepp.somun.member.entity.SocialProvider
//import com.grepp.somun.member.oauth.common.dto.SocialLoginRequest
//import com.grepp.somun.member.oauth.common.dto.SocialMemberCheckDto
//import com.grepp.somun.member.oauth.common.dto.SocialMemberInfoDto
//import com.grepp.somun.member.oauth.common.service.SocialClient
//import com.grepp.somun.member.oauth.kakao.service.KakaoClient
//import com.grepp.somun.member.oauth.naver.service.NaverClient
//import com.grepp.somun.member.service.MemberService
//import jakarta.servlet.http.HttpSession
//import org.slf4j.LoggerFactory
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
///**
// * 네이버,카카오 소셜 로그인 컨트롤러 통합
// */
//@RestController
//@RequestMapping("/api/v1/members/oauth")
//class OauthController(
//    private val memberService: MemberService,
//    private val kakaoClient: KakaoClient,
//    private val naverClient: NaverClient,
//    private val authService: AuthService
//) {
//
//    private val log = LoggerFactory.getLogger(OauthController::class.java)
//
//    /**
//     * provider를 구분하여 각각의 client에서 회원정보를 얻고, 가입 여부를 확인 후 로그인 또는 회원가입을 진행
//     */
//    @PostMapping("/{provider}")
//    @Throws(JsonProcessingException::class)
//    fun socialCallback(
//        @PathVariable("provider") provider: SocialProvider,
//        @RequestBody socialLoginRequest: SocialLoginRequest,
//        session: HttpSession
//    ): ResponseEntity<*> {
//        val clientByProvider = getClientByProvider(provider)
//            ?: throw GeneralException(ErrorStatus._BAD_REQUEST)
//
//        val accessToken = if (provider == SocialProvider.NAVER) {
//            clientByProvider.getAccessToken(socialLoginRequest.code, socialLoginRequest.state)
//        } else {
//            clientByProvider.getAccessToken(socialLoginRequest.code)
//        }
//
//        val memberInfo = clientByProvider.getMemberInfo(accessToken)
//
//        return if (memberService.isSocialMemberRegistered(memberInfo)) {
//            // 가입되어있으면 토큰 발급
//            val tokenResponseDTO = authService.createTokenResponseForSocialMember(memberInfo.email)
//            createTokenResponse(tokenResponseDTO)
//        } else {
//            // 소셜 사용자 회원가입시 필요한 기본정보 세션에 저장
//            session.apply {
//                setAttribute("providerId", memberInfo.providerId)
//                setAttribute("provider", memberInfo.provider)
//                setAttribute("email", memberInfo.email)
//                maxInactiveInterval = 600 // 임시 회원정보 세션 10분간 유지
//            }
//            ResponseEntity.ok("redirect nicknamePage")
//        }
//    }
//
//    /**
//     * 소셜 사용자 임시정보 세션과 사용자에게 받은 닉네임을 확인 후 회원가입을 진행
//     */
//    @PostMapping("/register")
//    fun register(
//        @RequestBody memberInfoDto: SocialMemberInfoDto,
//        session: HttpSession
//    ): ResponseEntity<*> {
//        memberService.validateNameAndCheckDuplicate(memberInfoDto.name)
//        val email = session.getAttribute("email") as? String
//        val providerId = session.getAttribute("providerId") as? String
//        val provider = session.getAttribute("provider") as? SocialProvider
//
//        if (email == null || provider == null || providerId == null) {
//            throw GeneralException(ErrorStatus.SOCIAL_INFO_INVALID)
//        }
//
//        val socialRegisterRequest = SocialRegisterRequest.create(email, providerId, memberInfoDto.name, provider)
//        memberService.registerSocialMember(socialRegisterRequest, session)
//        val tokenResponseDTO = authService.createTokenResponseForSocialMember(email)
//        return createTokenResponse(tokenResponseDTO)
//    }
//
//    // provider에 맞는 Client 가져오기
//    private fun getClientByProvider(provider: SocialProvider): SocialClient? {
//        return when (provider) {
//            SocialProvider.NAVER -> naverClient
//            SocialProvider.KAKAO -> kakaoClient
//            else -> null
//        }
//    }
//
//    // 소셜 사용자 로그인 후 토큰 발급
//    private fun createTokenResponse(tokenResponseDTO: TokenResponseDTO): ResponseEntity<TokenResponseDTO> {
//        val httpHeaders = HttpHeaders().apply {
//            add(JwtFilter.AUTHORIZATION_HEADER, "Bearer ${tokenResponseDTO.accessToken}")
//        }
//        return ResponseEntity(tokenResponseDTO, httpHeaders, HttpStatus.OK)
//    }
//}