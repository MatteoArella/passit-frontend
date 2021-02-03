package com.github.passit.domain.model

import java.net.URL

data class User(
    var email: String,
    var familyName: String,
    var givenName: String,
    var name: String? = null,
    var phoneNumber: String? = null,
    var birthDate: String? = null,
    var picture: URL? = null
)
