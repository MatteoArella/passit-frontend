package it.uniroma1.macc.project.repository

import android.app.Activity
import android.content.Intent
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.auth.AuthProvider
import com.amplifyframework.auth.AuthUserAttributeKey
import it.uniroma1.macc.project.data.repository.auth.*
import it.uniroma1.macc.project.domain.model.SignUpUserAttributes
import it.uniroma1.macc.project.domain.model.User
import it.uniroma1.macc.project.domain.repository.IdentityRepository
import it.uniroma1.macc.project.domain.usecase.exception.auth.SignInError
import kotlinx.coroutines.flow.*
import okhttp3.internal.notifyAll
import java.net.URL
import kotlin.coroutines.resumeWithException
import kotlin.jvm.Throws

class AWSIdentityRepository @Inject constructor() : IdentityRepository {
    override val currentUser = MutableLiveData<User?>(null)
    private val userAttributeKeys = mapOf<UserAttribute, AuthUserAttributeKey>(
        UserAttribute.EMAIL to AuthUserAttributeKey.email(),
        UserAttribute.FAMILY_NAME to AuthUserAttributeKey.familyName(),
        UserAttribute.GIVEN_NAME to AuthUserAttributeKey.givenName(),
        UserAttribute.NAME to AuthUserAttributeKey.name(),
        UserAttribute.PHONE to AuthUserAttributeKey.phoneNumber(),
        UserAttribute.PICTURE to AuthUserAttributeKey.picture(),
    )

    override suspend fun fetchUserAttributes(): User {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchUserAttributes(
                { result ->
                    run {
                        val attributesMap: Map<AuthUserAttributeKey, String> = result.associateBy({ it.key }, { it.value })
                        val user = User(
                            email = attributesMap[AuthUserAttributeKey.email()] ?: error("email not valid"),
                            givenName = attributesMap[AuthUserAttributeKey.givenName()] ?: error("givenName not valid"),
                            familyName = attributesMap[AuthUserAttributeKey.familyName()] ?: error("familyName not valid"),
                            name = attributesMap[AuthUserAttributeKey.name()],
                            phoneNumber = attributesMap[AuthUserAttributeKey.phoneNumber()]
                        ).apply {
                            picture = runCatching { URL(attributesMap[AuthUserAttributeKey.picture()]) }.getOrNull()
                        }
                        currentUser.postValue(user)
                        continuation.resume(user)
                    }
                },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun fetchAuthSession(): AuthSessionResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchAuthSession(
                { result -> continuation.resume(AuthSessionResult(result.isSignedIn)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    @Throws(SignInError::class)
    override suspend fun signIn(@NonNull email: String, @NonNull password: String): AuthSignInResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signIn(
                email,
                password,
                { result -> continuation.resume(AuthSignInResult(result.isSignInComplete)) },
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

    override suspend fun signInWithGoogle(@NonNull context: Activity): AuthSignInResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.signInWithSocialWebUI(
                AuthProvider.google(),
                context,
                { result -> continuation.resume(AuthSignInResult(result.isSignInComplete)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun handleFederatedSignInResponse(@NonNull data: Intent) {
        Amplify.Auth.handleWebUISignInResponse(data)
    }

    override suspend fun signUp(@NonNull email: String,
                       @NonNull password: String,
                       @NonNull attributes: SignUpUserAttributes): AuthSignUpResult {
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
                { result -> continuation.resume(AuthSignUpResult(result.isSignUpComplete)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun confirmSignUp(@NonNull email: String, @NonNull confirmationCode: String): AuthSignUpResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.confirmSignUp(
                email,
                confirmationCode,
                { result -> continuation.resume(AuthSignUpResult(result.isSignUpComplete)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun resendConfirmationCode(@NonNull email: String): AuthSignUpResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.resendSignUpCode(
                email,
                { result -> continuation.resume(AuthSignUpResult(result.isSignUpComplete)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun resetPassword(@NonNull email: String): AuthResetPasswordResult {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.resetPassword(
                email,
                { result -> continuation.resume(AuthResetPasswordResult(result.isPasswordReset)) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun confirmResetPassword(newPassword: String, confirmationCode: String) {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.confirmResetPassword(
                newPassword,
                confirmationCode,
                { continuation.resume(Unit) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun updateUserAttribute(attribute: UserAttribute, value: String) {
        return suspendCancellableCoroutine { continuation ->
            Amplify.Auth.updateUserAttribute(
                AuthUserAttribute(userAttributeKeys[attribute], value),
                { run {
                    currentUser.value?.let { user ->
                        when (attribute) {
                            UserAttribute.EMAIL -> user.email = value
                            UserAttribute.FAMILY_NAME -> user.familyName = value
                            UserAttribute.GIVEN_NAME -> user.givenName = value
                            UserAttribute.NAME -> user.name = value
                            UserAttribute.PHONE -> user.phoneNumber = value
                            UserAttribute.PICTURE -> user.picture = runCatching { URL(value) }.getOrNull()
                        }
                        currentUser.postValue(user)
                    }
                    continuation.resume(Unit)
                } },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }

    override suspend fun signOut() {
        return suspendCoroutine { continuation ->
            Amplify.Auth.signOut(
                { continuation.resume(Unit) },
                { error -> continuation.resumeWithException(error) }
            )
        }
    }
}

