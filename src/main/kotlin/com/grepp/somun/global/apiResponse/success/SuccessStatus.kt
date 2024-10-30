package com.grepp.somun.global.apiResponse.success



import org.springframework.http.HttpStatus

enum class SuccessStatus(val httpStatus: HttpStatus, val message: String) {
    _OK(HttpStatus.OK, "성공입니다."),
    _CREATED(HttpStatus.CREATED, "리소스가 성공적으로 생성되었습니다."),
    _NO_CONTENT(HttpStatus.NO_CONTENT, "콘텐츠가 없습니다.");
}
