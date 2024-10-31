package com.grepp.somun.member.service

import com.grepp.somun.global.apiResponse.exception.ErrorStatus
import com.grepp.somun.global.apiResponse.exception.GeneralException
import com.grepp.somun.member.validator.EmailValidator
import com.grepp.somun.member.validator.NameValidator
import com.grepp.somun.member.validator.PasswordValidator
import lombok.RequiredArgsConstructor

@org.springframework.stereotype.Service
@RequiredArgsConstructor
class MemberValidatorService (
    private val emailValidator: EmailValidator,
    private val nameValidator: NameValidator,
    private val passwordValidator: PasswordValidator
){

    fun validateEmail(email: String?) {
        if (!emailValidator.isValidEmail(email)) {
            throw GeneralException(ErrorStatus.EMAIL_INVALID)
        }
    }

    fun validateName(name: String?) {
        if (!nameValidator.isValidName(name)) {
            throw GeneralException(ErrorStatus.NAME_INVALID)
        }
    }

    fun validatePassword(password: String?) {
        if (!passwordValidator.isValidPassword(password)) {
            throw GeneralException(ErrorStatus.PASSWORD_INVALID)
        }
    }
}
