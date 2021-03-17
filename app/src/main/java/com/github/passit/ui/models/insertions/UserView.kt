package com.github.passit.ui.models.insertions

import java.net.URL

data class UserView(
    var id: String?,
    var email: String?,
    var familyName: String?,
    var givenName: String?,
    var phoneNumber: String?,
    var birthDate: String?,
    var picture: URL?
)