package com.grepp.somun.member.validator

import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class NameValidator {
    fun isValidName(name: String?): Boolean {
        if (name == null || name.trim { it <= ' ' }.isEmpty()) {
            return false
        }
        return NAME_PATTERN.matcher(name).matches()
    }

    companion object {
        // 3자이상 10자이하, (초성,모음 제한)한글, 영어, 숫자 가능
        private const val NAME_REGEX = "^(?=.{3,10}$)(?!.*[ㄱ-ㅎㅏ-ㅣ])[a-zA-Z0-9가-힣]*$"
        private val NAME_PATTERN: Pattern = Pattern.compile(NAME_REGEX)
    }
}
