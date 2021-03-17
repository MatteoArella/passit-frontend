package com.github.passit.ui.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.domain.model.auth.User
import com.github.passit.ui.models.insertions.UserView

object UserEntityToUIMapper: Mapper<User, UserView>() {
    override fun map(from: User): UserView {
        return UserView(
                id = from.id,
                email = from.email,
                familyName = from.familyName,
                givenName = from.givenName,
                phoneNumber = from.phoneNumber,
                birthDate = from.birthDate,
                picture = from.picture
        )
    }
}