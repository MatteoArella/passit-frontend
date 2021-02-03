package com.github.passit.domain.model

data class SignUpUserAttributes(
    val email: String,
    val familyName: String,
    val givenName: String,
    val phoneNumber: String?,
    val birthDate: String?
)