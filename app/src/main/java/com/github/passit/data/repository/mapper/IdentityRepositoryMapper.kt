package com.github.passit.data.repository.mapper

import com.github.passit.core.domain.Mapper
import com.github.passit.data.datasource.local.model.UserLocalData
import com.github.passit.data.datasource.remote.model.*
import com.github.passit.domain.model.auth.User
import com.github.passit.domain.model.auth.*
import java.net.URL

object UserRemoteToEntityMapper: Mapper<UserRemoteData, User>() {
    override fun map(from: UserRemoteData): User {
        return User(
                id = from.id ?: "",
                email = from.email ?: "",
                givenName = from.givenName ?: "",
                familyName = from.familyName ?: "",
                phoneNumber = from.phoneNumber,
                birthDate = from.birthDate,
                picture = runCatching { URL(from.picture) }.getOrNull()
        )
    }
}

object UserRemoteToLocalMapper: Mapper<UserRemoteData?, UserLocalData>() {
    override fun map(from: UserRemoteData?): UserLocalData {
        return UserLocalData(
                userId = from?.id ?: "",
                email = from?.email,
                familyName = from?.familyName,
                givenName = from?.givenName,
                phoneNumber = from?.phoneNumber,
                birthDate = from?.birthDate,
                picture = from?.picture
        )
    }
}

object UserLocalToEntityMapper: Mapper<UserLocalData, User>() {
    override fun map(from: UserLocalData): User {
        return User(
                id = from.userId,
                email = from.email ?: "",
                familyName = from.familyName ?: "",
                givenName = from.givenName ?: "",
                phoneNumber = from.phoneNumber,
                birthDate = from.birthDate,
                picture = from.picture?.let { runCatching { URL(it) }.getOrNull() }
        )
    }
}

object AuthSessionRemoteToEntityMapper: Mapper<AuthSessionRemoteData, AuthSession>() {
    override fun map(from: AuthSessionRemoteData): AuthSession {
        return AuthSession(
                from.isSignedIn
        )
    }
}

object AuthSignInRemoteToEntityMapper: Mapper<AuthSignInRemoteData, AuthSignIn>() {
    override fun map(from: AuthSignInRemoteData): AuthSignIn {
        return AuthSignIn(
                from.isSignedIn
        )
    }
}

object AuthSignUpRemoteToEntityMapper: Mapper<AuthSignUpRemoteData, AuthSignUp>() {
    override fun map(from: AuthSignUpRemoteData): AuthSignUp {
        return AuthSignUp(
                from.isSignedUpComplete
        )
    }
}

object AuthResetPasswordRemoteToEntityMapper: Mapper<AuthResetPasswordRemoteData, AuthResetPassword>() {
    override fun map(from: AuthResetPasswordRemoteData): AuthResetPassword {
        return AuthResetPassword(
                from.isPasswordResetComplete
        )
    }
}