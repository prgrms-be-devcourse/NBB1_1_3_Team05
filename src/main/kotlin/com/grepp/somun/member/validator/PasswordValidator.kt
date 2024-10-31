package com.grepp.somun.member.validator

import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class PasswordValidator {
    fun isValidPassword(password: String?): Boolean {
        if (password == null || password.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        return PASSWORD_PATTERN.matcher(password).matches()
    }

    companion object {
        // 숫자,문자,특수문자(정의된 특수문자만 사용가능)무조건 1개 이상씩, 총 8자이상 16자 이하 허용
        private const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])" +
                "[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$"
        private val PASSWORD_PATTERN: Pattern = Pattern.compile(PASSWORD_REGEX)
    }
}
