package com.github.passit.domain.model.auth

import com.github.passit.domain.model.Insertion
import java.net.URL

data class User(
    var id: String,
    var email: String,
    var familyName: String,
    var givenName: String,
    var phoneNumber: String?,
    var birthDate: String?,
    var picture: URL?,
    var insertions: List<Insertion>? = null
)
