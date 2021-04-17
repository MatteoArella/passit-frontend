package com.github.passit.data.datasource.remote

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.github.passit.data.datasource.remote.model.*
import com.github.passit.domain.model.auth.SignUpUserAttributes
import com.github.passit.domain.model.auth.UserAttribute
import com.github.passit.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IdentityRemoteDataSource @Inject constructor() {
    private val userAttributeKeys = mapOf<UserAttribute, AuthUserAttributeKey>(
            UserAttribute.EMAIL to AuthUserAttributeKey.email(),
            UserAttribute.FAMILY_NAME to AuthUserAttributeKey.familyName(),
            UserAttribute.GIVEN_NAME to AuthUserAttributeKey.givenName(),
            UserAttribute.PHONE to AuthUserAttributeKey.phoneNumber(),
            UserAttribute.PICTURE to AuthUserAttributeKey.picture(),
    )

    suspend fun fetchUserAttributes(): UserRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchUserAttributes(
                    { result ->
                        run {
                            val attributesMap: Map<AuthUserAttributeKey, String> = result.associateBy({ it.key }, { it.value })
                            val user = UserRemoteData(
                                    id = attributesMap[AuthUserAttributeKey.custom("sub")] ?: "",
                                    email = attributesMap[AuthUserAttributeKey.email()],
                                    givenName = attributesMap[AuthUserAttributeKey.givenName()],
                                    familyName = attributesMap[AuthUserAttributeKey.familyName()],
                                    phoneNumber = attributesMap[AuthUserAttributeKey.phoneNumber()],
                                    birthDate = attributesMap[AuthUserAttributeKey.birthdate()],
                                    picture = attributesMap[AuthUserAttributeKey.picture()]
                            )
                            continuation.resume(user)
                        }
                    },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun fetchAuthSession(): AuthSessionRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession(
                    { result -> continuation.resume(AuthSessionRemoteData(result.isSignedIn)) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    @Throws(SignInError::class)
    suspend fun signIn(@NonNull email: String, @NonNull password: String): AuthSignInRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signIn(
                    email,
                    password,
                    { result -> continuation.resume(AuthSignInRemoteData(result.isSignInComplete)) },
                    { error -> run {
                        if (Regex("UNCONFIRMED").containsMatchIn(error.cause?.message!!)) {
                            continuation.resumeWithException(SignInError.Unconfirmed(error))
                        } else {
                            continuation.resumeWithException(SignInError.WrongCredentials(error))
                        }
                    }
                    }
            )
        }
    }

    suspend fun signUp(@NonNull email: String,
                                @NonNull password: String,
                                @NonNull attributes: SignUpUserAttributes): AuthSignUpRemoteData {
        val authSignUpOptions = AuthSignUpOptions.builder()
                .userAttributes(
                        listOf(
                                AuthUserAttribute(
                                        AuthUserAttributeKey.email(),
                                        attributes.email
                                ),
                                AuthUserAttribute(
                                        AuthUserAttributeKey.familyName(),
                                        attributes.familyName
                                ),
                                AuthUserAttribute(
                                        AuthUserAttributeKey.givenName(),
                                        attributes.givenName
                                ),
                                AuthUserAttribute(
                                        AuthUserAttributeKey.phoneNumber(),
                                        attributes.phoneNumber
                                ),
                                AuthUserAttribute(
                                        AuthUserAttributeKey.birthdate(),
                                        attributes.birthDate
                                )
                        )
                ).build()
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signUp(
                    email,
                    password,
                    authSignUpOptions,
                    { result -> continuation.resume(AuthSignUpRemoteData(result.isSignUpComplete)) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): AuthSignUpRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.confirmSignUp(
                    email,
                    confirmationCode,
                    { result -> continuation.resume(AuthSignUpRemoteData(result.isSignUpComplete)) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun resendConfirmationCode(@NonNull email: String): AuthSignUpRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.resendSignUpCode(
                    email,
                    { result -> continuation.resume(AuthSignUpRemoteData(result.isSignUpComplete)) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun resetPassword(@NonNull email: String): AuthResetPasswordRemoteData {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.resetPassword(
                    email,
                    { result -> continuation.resume(AuthResetPasswordRemoteData(result.isPasswordReset)) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun confirmResetPassword(newPassword: String, confirmationCode: String) {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.confirmResetPassword(
                    newPassword,
                    confirmationCode,
                    { continuation.resume(Unit) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun updateUserAttribute(attribute: UserAttribute, value: String) {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.updateUserAttribute(
                    AuthUserAttribute(userAttributeKeys[attribute], value),
                    { continuation.resume(Unit) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }

    suspend fun signOut() {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signOut(
                    { continuation.resume(Unit) },
                    { error -> continuation.resumeWithException(error) }
            )
        }
    }
}