package com.grepp.somun.member.validator

import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class EmailValidator {
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }
        return EMAIL_PATTERN.matcher(email).matches()
    }

    companion object {
        private const val EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*" +
                "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        private val EMAIL_PATTERN: Pattern = Pattern.compile(EMAIL_REGEX)
    }
}
